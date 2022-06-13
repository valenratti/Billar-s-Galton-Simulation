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
    error = 0

    for i in range(len(data1)):
        error += pow(data1[i] - data2[i], 2)

    return error


# Press the green button in the gutter to run the script.
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

    for N in Ns:
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

        a1 = plots.histogram_and_pdf(data, bins_limits, density=False)
        data_density, normal_fit = plots.histogram_and_pdf(data, bins_limits, first_bin_position)

        print(f'mean: {np.mean(normal_fit)}, std: {np.std(normal_fit)}')
        print(a1)

        pdf = norm.pdf(bins_positions_center, mean, std)

        squared_error_list.append(squared_error(data_density, pdf))

    # Error del ajuste vs n
    plots.error_vs_n(Ns, squared_error_list)
    print(squared_error_list)
