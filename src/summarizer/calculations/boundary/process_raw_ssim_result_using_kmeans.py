import matplotlib.pyplot as plt
import numpy as np
from sklearn.cluster import KMeans
import collections

N_CLUSTER = 10

if __name__ == '__main__':
    x_axis_list = []
    y_axis_list = []

    f = open('soccer_ssim_raw.txt', 'r')

    mat = []
    x_only_mat = []

    for x in f:
        y_axis = float(x.split(' ')[0])
        x_axis = float(x.split(' ')[1])

        x_axis_list.append(x_axis)
        y_axis_list.append(y_axis / 1000.0)

        x_only_mat.append([x_axis])
        mat.append([x_axis, y_axis])

        # if len(x_axis_list) == 5000:
        #     break

    mat = np.array(mat)
    x_only_mat = np.array(x_only_mat)

    # kmeans
    cluster = KMeans(n_clusters=N_CLUSTER)
    cluster.fit(x_only_mat)
    cluster_labels = cluster.predict(x_only_mat)

    # get labels with the least count
    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th1 = label_counts[-1][0]
    th2 = label_counts[-2][0]

    # print boundaries
    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if curr_label == th1:
            print(i)

    plt.figure(figsize=(10, 7))
    # plt.scatter(mat[:, 0], mat[:, 1], c= y_kmeans, cmap='rainbow')
    plt.scatter(x_axis_list, y_axis_list, c=cluster_labels, cmap='rainbow')
    plt.show()