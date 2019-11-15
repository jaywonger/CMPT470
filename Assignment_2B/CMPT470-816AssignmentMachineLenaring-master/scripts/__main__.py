from __future__ import absolute_import, division, print_function, unicode_literals
import config,os,logging,warnings
from vocabulary import Vocabulary
import pandas as pd
from glob import glob as glob
from logging import config as logging_config
from tensorflow import keras
from sklearn.model_selection import train_test_split
from tensorflow.keras import layers
from keras.preprocessing.sequence import pad_sequences



config.init()
warnings.filterwarnings('ignore')
keras.backend.clear_session()

logging.basicConfig(filename=config.log_file_path,
                    filemode='a',
                    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
                    datefmt= '%H:%M:%S',
                    level=logging.INFO)

# skips this line for some reason
logging_config.fileConfig(fname='log.conf', defaults={'logfilename': config.log_file_path},disable_existing_loggers=False)
logger = logging.getLogger(__name__)


def collectDataset(files):
    '''
    The method collects the data from the list of files and add them to the pandas dataframe
    :param files: list of files
    :return: pandas dataframe that have all data of the files
    '''
    data = []
    for each_file in files:
        df = pd.read_csv(each_file,error_bad_lines=False)
        data.append(df)
    data = pd.concat(data)
    return data

def encodeSentences(sentences,lang):
    '''
    The method encodes each sentence in the sentences list into a integer list based on the language.
    :param sentences: A list of string
    :param lang: An object of a language
    :param isLabel: Will the function be operated for label or context?
    :return: A list of list where each list is the encoded version of a sentence
    '''
    encoded_sentences = []
    for each_sentence in sentences:
        encoded_each_sentence = []
        for word in each_sentence.strip().split(' '):
            if word in lang.word2index:
                encoded_each_sentence.append(lang.word2index[word])
            else:
                encoded_each_sentence.append(config.UNK_token)
        encoded_sentences.append(encoded_each_sentence)
    return encoded_sentences


#----------------------------------------------------------------------------------------------------------------------
# 1. Data Collection
#----------------------------------------------------------------------------------------------------------------------
print('1. Data Colelction....')
logger.info("Collecting Dataset.....")
files = [y for x in os.walk(config.full_dataset_folder_path) for y in glob(os.path.join(x[0], '*.csv'))]
train_files, test_files = train_test_split(files, test_size=0.1)

df_train = collectDataset(train_files)
df_test = collectDataset(test_files)
logger.info("Number of Training data: %d", len(df_train))
logger.info("Number of Testing data: %d", len(df_test))
logger.info('Data Colelction Done')

#----------------------------------------------------------------------------------------------------------------------
# 2. Data Preprocessing
#----------------------------------------------------------------------------------------------------------------------
print('2. Data Preprocessing....')
logger.info("Data Preprocessing for train dataset startting.....")
logger.info("Creating input vocabulary for context and output vocabulary for lable/method name")
input_vocab = Vocabulary("Context")
output_vocab = Vocabulary("label")

contexts = []
labels = []
for index, row in df_train.iterrows():
    input_vocab.addSentence(row['Context'])
    contexts.append(row['Context'])
    output_vocab.addSentence(row['Label'])
    labels.append(row['Label'])
logger.info("Number of word in input vocabulary: %d", input_vocab.n_words)
logger.info("Number of word in output vocabulary: %d", output_vocab.n_words)
logger.info("Removing the words that appear less than %d in input and output vocabulary", config.min_frequency)
input_vocab.removeWordLessThan(config.min_frequency)
output_vocab.removeWordLessThan(config.min_frequency)
logger.info("After Filtering")
logger.info("Number of word in input vocabulary: %d", input_vocab.n_words)
logger.info("Number of word in output vocabulary: %d", output_vocab.n_words)

context_vocab_size = input_vocab.n_words
label_vocab_size = output_vocab.n_words
logger.info("Context and label for training in encoded....")
train_contexts_encoded = encodeSentences(contexts,input_vocab)
train_label_encoded = encodeSentences(labels,output_vocab)
logger.info("Context for training in padded....")
train_contexts_padded = pad_sequences(train_contexts_encoded, maxlen=config.max_input_length, padding='post',value=config.PADDED_Token)
train_label_padded = pad_sequences(train_label_encoded, maxlen=1, padding='post',value=config.PADDED_Token)
logger.info("Data Preprocessing Done...")

#----------------------------------------------------------------------------------------------------------------------
# 3. Neural Network Configuration
#----------------------------------------------------------------------------------------------------------------------
print('3. Neural Network Configuration....')
# define the model
logger.info("Configuring Neural network model")
model = keras.Sequential()
model.add(layers.Embedding(input_dim=context_vocab_size, output_dim=256, input_length=config.max_input_length))
model.add(layers.LSTM(units=128))      #change to LSTM here
model.add(layers.Dense(units=label_vocab_size, activation='sigmoid'))
# summarize the model
model.summary()
# compile the model
model.compile(optimizer=config.optimizer, loss=config.loss_function, metrics=config.metrics)
logger.info('Neural Network Configuration Done')
#----------------------------------------------------------------------------------------------------------------------
# 4. Training
#----------------------------------------------------------------------------------------------------------------------
print('4. Training....')
# fit the model
logger.info("Training is starting......")
model.fit(x=train_contexts_padded, y=train_label_padded, batch_size = config.batch_size, epochs=config.epochs)
logger.info('Training Done')
#----------------------------------------------------------------------------------------------------------------------
# 5. Testing
#----------------------------------------------------------------------------------------------------------------------
print('5. Testing....')
logger.info("Data Preprocessing for test dataset startting.....")
contexts = []
labels = []
for index, row in df_test.iterrows():
    contexts.append(row['Context'])
    labels.append(row['Label'])

logger.info("Context and label for testing in encoded....")
test_contexts_encoded = encodeSentences(contexts,input_vocab)
test_label_encoded = encodeSentences(labels,output_vocab)
logger.info("Context for testing in padded....")
test_contexts_padded = pad_sequences(test_contexts_encoded, maxlen=config.max_input_length, padding='post',value=config.PADDED_Token)
test_label_padded = pad_sequences(test_label_encoded, maxlen=1, padding='post',value=config.PADDED_Token)

# evaluate the model
logger.info("Testing is starting......")
loss, accuracy = model.evaluate(x=test_contexts_padded, y=test_label_padded,verbose=0)

logger.info('Accuracy: %f',(accuracy*100))
logger.info('Testing Done')
