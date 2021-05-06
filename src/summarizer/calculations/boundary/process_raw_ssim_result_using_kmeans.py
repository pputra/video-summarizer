import matplotlib.pyplot as plt
import numpy as np
from sklearn.cluster import KMeans
import collections

INITIAL_N_CLUSTER = 8
INITIAL_CLUSTER_TO_INCLUDE = 1

N_SUB_CLUSTERS = [3, 5, 4, 4]
NEXT_SUB_CLUSTERS_TO_INCLUDE = [1, 2, 1, 1]


result = []


def write_boundaries(res):
    f = open('out.txt', 'w')

    for i in range(len(res)):
        f.write(str(res[i]) + '\n')

    f.close()


def get_n_smallest_cluster_labels(cluster_labels, num_last_n_clusters):
    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th_set = set()

    for i in range(num_last_n_clusters):
        th = label_counts[-1 * (i+1)][0]
        th_set.add(th)
    return th_set


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

    # initial kmeans to to remove non outliers
    cluster = KMeans(n_clusters=INITIAL_N_CLUSTER)
    cluster.fit(x_only_mat)
    cluster_labels = cluster.predict(x_only_mat)

    outlier_label_set = get_n_smallest_cluster_labels(cluster_labels, INITIAL_CLUSTER_TO_INCLUDE)

    sub_mat = []

    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if outlier_label_set.__contains__(curr_label):
            sub_mat.append([x_axis_list[i], i])

    # recursively perform kmeans to filter outliers
    sub_mat = np.array(sub_mat)
    for i in range(len(N_SUB_CLUSTERS)):
        cluster = KMeans(n_clusters=N_SUB_CLUSTERS[i])
        cluster.fit(sub_mat)
        cluster_labels = cluster.predict(sub_mat)
        outlier_label_set = get_n_smallest_cluster_labels(cluster_labels, NEXT_SUB_CLUSTERS_TO_INCLUDE[i])

        next_sub_mat = []
        for j in range(len(cluster_labels)):
            curr_label = cluster_labels[j]
            if outlier_label_set.__contains__(curr_label):
                result.append(sub_mat[j][1])
            else:
                next_sub_mat.append(sub_mat[j])
        if i == len(N_SUB_CLUSTERS) - 1:
            break
        sub_mat = np.array(next_sub_mat)

    plt.figure(figsize=(10, 7))
    plt.scatter(sub_mat[:, 0], sub_mat[:, 1], c=cluster_labels, cmap='rainbow')

    result.sort()
    write_boundaries(result)

    plt.show()
