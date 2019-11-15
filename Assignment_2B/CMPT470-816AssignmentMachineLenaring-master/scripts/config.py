def init():
    global \
        full_dataset_folder_path,\
        result_file_path, \
        log_file_path, \
        PADDED_Token, \
        UNK_token,\
        dataset_head, \
        min_frequency,\
        max_input_length,\
        loss_function,\
        optimizer,\
        metrics,\
        batch_size,\
        epochs

    # All File Path
    root_folder = '../'

    full_dataset_folder_path = root_folder + 'data/dataset/'

    result_file_path = root_folder + 'data/results.csv'

    log_file_path = root_folder+'jaw845_keras_lstm.log'


    #Data Preprocessing:
    PADDED_Token = 0
    UNK_token = 1
    min_frequency = 1
    max_input_length = 10

    # Neural Network parameter:
    loss_function = 'sparse_categorical_crossentropy'
    optimizer = 'adam'
    metrics = ['accuracy']
    batch_size = 32
    epochs = 10




    dataset_head=['FilePath' ,
                 'LineNumber',
                 'RecieverVaribale',
                 'Context',
                 'Label']
