import matplotlib.pyplot as plt
import numpy as np
from sklearn.cluster import KMeans
import collections

N_CLUSTER1 = 8
N_CLUSTER2 = 3
N_CLUSTER3 = 5
N_CLUSTER4 = 4

result = []

def write_boundaries(res):
    f = open('out.txt', 'w')

    for i in range(len(res)):
        f.write(str(res[i]) + '\n')

    f.close()

if __name__ == '__main__':
    x_axis_list = []
    y_axis_list = []

    f = open('concert_ssim_raw.txt', 'r')

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
    cluster = KMeans(n_clusters=N_CLUSTER1)
    cluster.fit(x_only_mat)
    cluster_labels = cluster.predict(x_only_mat)

    # get labels with the least count
    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th1 = label_counts[-1][0]
    th2 = label_counts[-2][0]

    # f = open('out.txt', 'w')
    start = 0
    end = 0

    sec_mat = []

    # print boundaries
    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if curr_label == th1:
            sec_mat.append([x_axis_list[i], i])
            end = i
            boundary = str(start) + ' ' + str(i) + '\n'
            # f.write(boundary)
            # print(boundary)
            start = i + 1

    # if end != 16199:
    #     f.write(str(start) + ' 16199\n')
    #
    # f.close()

    plt.figure(figsize=(10, 7))

    # plt.scatter(x_axis_list, y_axis_list, c=cluster_labels, cmap='rainbow')

    # start 2

    sec_mat = np.array(sec_mat)


    cluster = KMeans(n_clusters=N_CLUSTER2)
    cluster.fit(sec_mat)
    cluster_labels = cluster.predict(sec_mat)

    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th1 = label_counts[-1][0]
    th2 = label_counts[-2][0]

    third_mat = []
    # print boundaries
    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if curr_label == th1:
            result.append(sec_mat[i][1])
            print(sec_mat[i][1])
        else:
            third_mat.append(sec_mat[i])

    # plt.scatter(sec_mat[:, 0], sec_mat[:, 1], c=cluster_labels, cmap='rainbow')

    # start 3

    third_mat = np.array(third_mat)

    cluster = KMeans(n_clusters=N_CLUSTER3)
    cluster.fit(third_mat)
    cluster_labels = cluster.predict(third_mat)

    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th1 = label_counts[-1][0]
    th2 = label_counts[-2][0]
    th3 = label_counts[-3][0]

    fourth_mat = []

    # print boundaries
    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if curr_label == th1 or curr_label == th2:
            result.append(third_mat[i][1])
            print(third_mat[i][1])
        else:
            fourth_mat.append(third_mat[i])

    # plt.scatter(third_mat[:, 0], third_mat[:, 1], c=cluster_labels, cmap='rainbow')


    # start 4
    fourth_mat = np.array(fourth_mat)

    cluster = KMeans(n_clusters=N_CLUSTER4)
    cluster.fit(fourth_mat)
    cluster_labels = cluster.predict(fourth_mat)

    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th1 = label_counts[-1][0]
    th2 = label_counts[-2][0]
    th3 = label_counts[-3][0]

    # print boundaries
    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if curr_label == th1:
            result.append(fourth_mat[i][1])
            print(fourth_mat[i][1])

    plt.scatter(fourth_mat[:, 0], fourth_mat[:, 1], c=cluster_labels, cmap='rainbow')

    result.sort()
    write_boundaries(result)

    plt.show()
