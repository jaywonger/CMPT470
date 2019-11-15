# CMPT470-816Assignment
This Repository contains the code for the machine learning of Task 1. To run the code do the following:

### Steps
1. Run the following command in the terminal/command prompt to install all dependencies:
    ```
    pip install -r requirements.txt
    ```

    If your default python is 2.7, you need to write following command to install all the dependencies in python 3.x:
    
    ```
    pip3 install -r requirements.txt
    ```

    However, if you dont know the default version of python, you can find it by witting the following command on terminal:
    
    ```
    python --version
    ```
    If the see the default version of python is 2.7, you can check whether you have python 3.x in you computer using the following command:
    
    ```
    python3 --version
    ```
    
    If you dont have python 3.x, please install it first. You will find all details in [this](https://www.python.org/downloads/) website.

2. Once all your dependencies get installed, you can use [Pycharm](https://www.jetbrains.com/pycharm/) editor or terminal to run the machine learning code. However, before running the code, you should change the following global variable in the [config.py](config.py) file:
    ```
    log_file_path = root_folder+'cms500_keras_rnn.log'
    ``` 

3. Now, run the [main.py](__main__.py) file either using Pycharm editor or using the following command:
   ```
   python __main__.py
   or
   python3 __main__.py
   ```
   After the successful run, you will find **your_nsid_keras_rnn.log** file in the project directory where all log information are stored along with the accuracy of the trained rnn model.

### Issues

If  you have any problem with the code provided in the repository. Please create an issue. I will try to solve it as soon as possible.

## Authors

* **C M Khaled Saifullah** - *Initial work* - [khaledkucse](https://github.com/khaledkucse)

See also the list of [contributors](https://github.com/khaledkucse/CMPT470-816AssignmentMachineLenaring/graphs/contributors) who participated in this project.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details

