import matplotlib.pyplot as plt
import numpy as np
import sys
import os.path

# Load data
print(os.path.exists(sys.argv[1]))
data = np.loadtxt(sys.argv[1], delimiter=',', skiprows=0)
column_values = np.abs(data[:])


# Number of bins
number_of_bins = 5


# Create the histogram
plt.hist(column_values, bins=np.arange(min(column_values), max(column_values)+0.1, step=0.1), color='turquoise')
plt.title('Histogram of P Values')
plt.xlabel('Value')
plt.ylabel('Frequency')


# Save the plot
plt.savefig(sys.argv[2])
print('Save Histogram Image : Completed')
