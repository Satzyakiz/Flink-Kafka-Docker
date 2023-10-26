import pandas as pd
data = pd.read_csv('/Users/satyakiz/Documents/SBU_Courses/AP/docker/AoT_Chicago.complete.temp1/data.csv',nrows=1000)
data_list = data.values.tolist()
data.to_csv('/Users/satyakiz/Documents/SBU_Courses/AP/docker/AoT_Chicago.complete.temp1/smallerDataset.csv', index=False)
data2 = pd.read_csv('/Users/satyakiz/Documents/SBU_Courses/AP/docker/AoT_Chicago.complete.temp1/smallerDataset.csv')
data_list2 = data2.values.tolist()
print(len(data_list2))