import config

class Vocabulary:
    def __init__(self, name=None):
        '''
        Constructor of vocabulary class.
        :param name: name of the vocabulary
        '''
        if(name == None):
            self.name = name
            self.word2index = {}
            self.word2count = {}
            self.index2word = {}
            self.n_words = 0

        else:
            self.name = name
            self.word2index = {"PAD":config.PADDED_Token,"UNK":config.UNK_token}
            self.word2count = {"PAD":1,"UNK":1}
            self.index2word = {config.PADDED_Token:"PAD",config.UNK_token:"UNK"}
            self.n_words = config.UNK_token+1  # Count SOS and EOS

    def addSentence(self, sentence):
        '''
        Add each word of the sentence in the vocabulary
        :param sentence: string seperated by space
        :return: void
        '''
        for word in sentence.strip().split(' '):
            self.addWord(word.strip())

    def addWord(self, word):
        '''
        Add one word to the vocabulary
        :param word: string that need to be added
        :return: void
        '''
        if word not in self.word2index:
            self.word2index[word] = self.n_words
            self.word2count[word] = 1
            self.index2word[self.n_words] = word
            self.n_words += 1
        else:
            self.word2count[word] += 1

    def removeWord(self,word):
        '''
        Remove a word from the vocabulary
        :param word: string that need to be removed
        :return: void
        '''
        if word in self.word2index:
            index = self.word2index[word]
            del self.word2index[word]
            del self.word2count[word]
            del self.index2word[index]
            self.n_words -= 1
        else:
            print("Cant Find the Word %s in the Dictonary" % word )

    def removeWordLessThan(self,frequency):
        '''
        Remove the words from the vocabulary that appear less than the frequency
        :param frequency: integer value representing the threshold
        :return: void
        '''
        removed_words = []
        for word,freq in self.word2count.items():
            if freq < frequency:
                removed_words.append(word)
        for each_word in removed_words:
            self.removeWord(each_word)