import pandas
import numpy as np
from scipy.stats import norm

import plots

path = "./outputs/"


def read_csv_columns_to_lists(file_name, column_names):
    data_ = pandas.read_csv(file_name)
    lists_ = []

    # converting column data to list
    for name in column_names:
        lists_.append(data_[name].tolist())

    return lists_


def squared_error(data1, data2):
    sq_error = 0

    for i in range(len(data1)):
        sq_error += pow(data1[i] - data2[i], 2)

    return sq_error


def find_best_c(data_density_, c_list_, x_list, mean_):
    best_c_ = 0
    min_error = float('inf')
    squared_error_list_ = []

    for c in c_list_:   # c = std
        norm_pdf = norm.pdf(x_list, mean_, c)
        squared_error_ = squared_error(data_density_, norm_pdf)

        squared_error_list_.append(squared_error_)
        if squared_error_ < min_error:
            min_error = squared_error_
            best_c_ = c

    return best_c_, min_error, squared_error_list_


if __name__ == '__main__':
    print('Running...')

    Ns = [300, 495, 690, 990]

    bin_width = 5.4   # cm
    bin_qty = 22
    first_bin_position = -59.4
    bins_positions_center = [first_bin_position + i * bin_width + bin_width / 2 for i in range(bin_qty)]
    bins_limits = [first_bin_position + n * bin_width for n in range(bin_qty + 1)]
    # range(bin_qty + 1) pq necesita los limites de cada bin, incluyendo el izq del 1ro y el der del ultimo

    col_names = ['bin_start', ' bin_end']
    # lists = read_csv_columns_to_lists(f'{path}bins.csv', col_names)
    # bins = lists[0]

    col_names = ['particle_id', ' particle_x_position']

    squared_error_list = []
    limits = [(102, 127), (125, 150), (142, 173), (174, 208)]

    for i in range(len(Ns)):
        N = Ns[i]
        # for i in range(5):
        lists = read_csv_columns_to_lists(f'{path}end_positions-{str(N)}.csv', col_names)
        data = lists[1]

        data = [d * 100 for d in data]  # from m to cm

        # fake data
        # mu = 0
        # sigma = 15
        # data = np.random.normal(mu, sigma, N)

        # Media y desvio
        mean = np.mean(data)
        std = np.std(data)
        print(f'mean: {mean}, std: {std}')

        # Para cada N:
        # - histograma (o puntos) con ajuste PDF (a mano y con software)
        # - informar error, std y mean (barra central)
        # - grafico c del ajuste

        a1, gbg = plots.histogram_and_pdf(data, bins_limits, density=False)

        s = sum(a1)
        data_density = [a / (bin_width * s) for a in a1]

        lim = limits[i]
        c_list = [c / 10 for c in range(lim[0], lim[1])]  # [..., 100, 100.1, 100.2, ...]
        best_c, min_err, c_error_list = find_best_c(data_density, c_list, bins_positions_center, mean)
        plots.c_plot(c_list, c_error_list, best_c, min_err, N)

        data_density2, normal_fit = plots.histogram_and_pdf(data, bins_limits, best_c)

        if squared_error(data_density, data_density2) > 1e-20:
            raise 'Error data density'

        # print(f'mean: {np.mean(normal_fit)}, std: {np.std(normal_fit)}')
        print(a1)

        squared_error_list.append(min_err)

    # Error del ajuste vs n
    plots.error_vs_n(Ns, squared_error_list)
    print(squared_error_list)
