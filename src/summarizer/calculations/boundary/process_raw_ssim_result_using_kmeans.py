import matplotlib.pyplot as plt
import numpy as np
from sklearn.cluster import KMeans
import collections

INITIAL_N_CLUSTER = 8
INITIAL_CLUSTER_TO_INCLUDE = 2
INITIAL_RESULT_CLUSTER_SIZE_THRESHOLD = 70

N_SUB_CLUSTERS = [3, 4, 4, 4, 3]
MAX_DEPTH = 4
MIN_SIZE_FOR_RESULT = [90, 10, 5, 5, 5]

result = []

def write_boundaries(res):
    f = open('out.txt', 'w')

    start = 0
    for i in range(len(res)):
        f.write(str(start) + ' ' + str(int(res[i])) + '\n')
        start = int(res[i]) + 1

    if res[len(res) - 1] != 16199:
        f.write(str(start) + ' ' + str(16199) + '\n')

    f.close()


def get_n_smallest_cluster_labels(cluster_labels, num_last_n_clusters):
    label_counts = collections.Counter(cluster_labels).most_common()
    print(label_counts)
    th_set = set()

    for i in range(num_last_n_clusters):
        th = label_counts[-1 * (i+1)][0]
        th_set.add(th)
    return th_set


def show_plot(mat, cluster_labels):
    plt.scatter(mat[:, 0], mat[:, 1], c=cluster_labels, cmap='rainbow')
    plt.show()


def recursive_kmeans(depth, mat):
    if depth == MAX_DEPTH:
        return

    cluster = KMeans(n_clusters=N_SUB_CLUSTERS[depth])
    cluster.fit(mat)
    cluster_labels = cluster.predict(mat)
    cluster_res = collections.Counter(cluster_labels).most_common()
    print(depth)
    print(cluster_res)
    result_set = set()
    for i in range(len(cluster_res)):
        label, size = cluster_res[i]
        if size < MIN_SIZE_FOR_RESULT[depth]:
            result_set.add(label)

    dict = {}

    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if result_set.__contains__(curr_label):
            result.append(mat[i][1])
        else:
            if curr_label in dict.keys():
                dict[curr_label].append(mat[i])
            else:
                dict[curr_label] = [mat[i]]
    for key in dict.keys():
        if len(dict[key]) > 0:
            sub_mat = np.array(dict[key])
            recursive_kmeans(depth + 1, sub_mat)


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

    for i in range(INITIAL_CLUSTER_TO_INCLUDE):
        label_counts = collections.Counter(cluster_labels).most_common()
        cluster_size = label_counts[-1 * (i + 1)][1]
        cluster_label = label_counts[-1 * (i + 1)][0]

        if cluster_size < INITIAL_RESULT_CLUSTER_SIZE_THRESHOLD:
            for j in range(len(cluster_labels)):
                curr_label = cluster_labels[j]
                if curr_label == cluster_label:
                    result.append(mat[j][1])
            outlier_label_set.remove(cluster_label)

    # result.sort()
    # write_boundaries(result)
    # show_plot(mat, cluster_labels)

    for i in range(len(cluster_labels)):
        curr_label = cluster_labels[i]
        if outlier_label_set.__contains__(curr_label):
            sub_mat.append([x_axis_list[i], i])

    # recursively perform kmeans to filter outliers
    if len(sub_mat) > 0:
        sub_mat = np.array(sub_mat)
        recursive_kmeans(0, sub_mat)

    result.sort()
    print(result)
    write_boundaries(result)
