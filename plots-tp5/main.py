import pandas
import numpy as np

import plots

tp5_path = "../../TP5/outputs/"


def read_csv_columns_to_lists(file_name, col_names):
    data = pandas.read_csv(file_name)
    lists = []

    # converting column data to list
    for name in col_names:
        lists.append(data[name].tolist())

    return lists


def ej1():
    print("Plots ejercicio 1...")
    ej_path = tp5_path + "ej1/"
    col_names = ['time', ' caudal']

    times = []
    flows = []
    mean_list = []
    stdev_list = []

    for D in Ds:
        lists = read_csv_columns_to_lists(f'{ej_path}caudal-{str(D)}.csv', col_names)
        times.append(lists[0])
        flow = lists[1]
        flows.append(flow)
        mean_list.append(np.mean(flow))
        stdev_list.append(np.std(flow))

    plots.time_vs_sth(Ds, times, flows, 'Caudal [partículas/s]', 'Evolución temporal del caudal', 'Apertura D [m]')
    plots.mean_and_stdev(Ds, mean_list, stdev_list, 'Valor medio y desviación estándar del caudal',
                         'Apertura D [m]', 'Caudal [partículas/s]')

    return mean_list, stdev_list


def ej2():
    print("Plots ejercicio 2...")
    ej_path = tp5_path + "ej2/"

    beverloo_flows = []

    for i in range(len(Ds)):
        mean = mean_list[i]
        Q, c, error = beverloo(Ds[i], mean)     # lo mas logico seria que c dé entre 0.5 y 2
        beverloo_flows.append(Q)
        print(f'D = {Ds[i]}\n'
              f'mean = {mean:.3f}, Q = {Q:.3f}, c = {c}, relative error = {error / mean:.3f}, '
              f'percentage error = {error / mean * 100:.3f} %\n')

    best_c = 0
    min_error = float('inf')
    square_error = 0
    square_error_list = []
    c_list = [c/10 for c in range(10, 36)]

    # finding best_c for all Ds
    for c2 in c_list:
        square_error = 0
        for i in range(len(Ds)):
            Q = beverloo_formula(Ds[i], c2)
            square_error += pow(mean_list[i] - Q, 2)

        square_error_list.append(square_error)
        if square_error < min_error:
            min_error = square_error
            best_c = c2

    beverloo_flows = [beverloo_formula(D, best_c) for D in Ds]

    print(f'c: {best_c}, error cuadratico: {min_error}')

    for i in range(len(Ds)):
        print(f'Q: {mean_list[i]}, bev: {beverloo_flows[i]}')

    plots.mean_and_stdev(Ds, mean_list, stdev_list, title=f'Ajuste del parámetro libre de la ley de Beverloo',
                         xlabel='Apertura D [m]', ylabel='Caudal [partículas/s]',
                         suptitle=f'C = {best_c}', beverloo=True, beveerloo_flows=beverloo_flows)
    plots.mse(c_list, square_error_list, best_c, min_error)


def beverloo(d, mean):
    min_error = float('inf')
    best_c = 0
    best_Q = 0

    for c in range(50):     # [0, 5]
        c = c / 10          # step 0.1
        Q = beverloo_formula(d, c)
        error = abs(Q - mean)
        if error < min_error:
            min_error = error
            best_c = c
            best_Q = Q

    return best_Q, best_c, min_error    # lo mas logico seria que c dé entre 0.5 y 2


def beverloo_formula(d, c):
    n = 10 * 33             # FIXME: chequear # nro de particulas que entran en el silo
    area = 1 * 0.3          # FIXME: chequear # area del silo
    n_p = n / area          # nro de particulas por unidad de volumen
    g_square_root = 3.13    # raiz de la gravedad
    r = (0.01 + 0.015) / 2  # radio medio de las particulas
    exp = 1.5               # exponente para silo 2D (para 3D es 2.5)

    return n_p * g_square_root * pow(d - c * r, exp)


def ej3():
    print("Plots ejercicio 3...")
    ej_path = tp5_path + "ej3/"
    col_names = ['time', ' energy']

    times = []
    energies = []

    for D in Ds:
        lists = read_csv_columns_to_lists(f'{ej_path}energies-{str(D)}-200000.0.csv', col_names)
        times.append(lists[0])
        energies.append(lists[1])
        # if D == 0.22:
        #     for i in range(len(times[2])):
        #         if times[2][i] > 3 and energies[2][i] > 6:
        #             print(f't: {times[2][i]}, e: {energies[2][i]}')

    plots.time_vs_sth(Ds, times, energies, 'Energía [J]', 'Evolución temporal de la energía cinética',
                      'Apertura D [m]', log_scale=True, ej3=True)


def ej4():
    print("Plots ejercicio 4...")
    ej_path = tp5_path + "ej4/"
    col_names = ['time', ' energy']

    kt_list = [100000, 300000, 500000, 1000000]
    times = []
    energies = []

    for kt in kt_list:
        lists = read_csv_columns_to_lists(f'{ej_path}energies-0.0-{str(kt)}.0.csv', col_names)
        times.append(lists[0])
        energies.append(lists[1])

    # energia residual
    stable_t = 0.44
    t = next(x for x in times[0] if x >= stable_t)
    i = times[0].index(t)
    trimmed_energies = [e[i:] for e in energies]

    e_mean_list = []
    e_stdev_list = []

    for j in range(len(kt_list)):
        e_mean_list.append(np.mean(trimmed_energies[j]))
        e_stdev_list.append(np.std(trimmed_energies[j]))

    plots.time_vs_sth(kt_list, times, energies, 'Energía [J]', 'D = 0', 'Parametro de fricción [N/m]',
                      suptitle='Evolución temporal de la energía cinética', log_scale=True)
    plots.mean_and_stdev(kt_list, e_mean_list, e_stdev_list,
                         'Energía residual promedio en función del parámetro de fricción',
                         'Parámetro de fricción [N/m]', 'Energía [J]')


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    Ds = [0.15, 0.18, 0.22, 0.24]

    mean_list, stdev_list = ej1()
    ej2()
    ej3()
    ej4()
