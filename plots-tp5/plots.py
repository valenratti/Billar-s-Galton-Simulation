from matplotlib import pyplot as plt, colors
import numpy as np


def time_vs_sth(labels, times, y_data, y_label, title, legend_title,
                suptitle=None, log_scale=False, zoom=False, ej3=False):  # energy or flow
    aux = [format(n, '.0e') for n in labels]
    for i in range(len(labels)):
        plt.plot(times[i], y_data[i], label=aux[i])

    plt.xlabel('Tiempo [s]')
    plt.ylabel(y_label)
    plt.suptitle(suptitle)
    plt.title(title)

    if zoom:
        plt.ylim(1e-1, 1e1)

    # plt.xlim(0.4, 2.1)
    # plt.ylim(0, 0.02)

    if log_scale:
        plt.yscale("symlog")   # FIXME: log o symlog?

    plt.legend(loc='center left', bbox_to_anchor=(1, 0.5), title=legend_title)
    plt.tight_layout()
    plt.show()


def mean_and_stdev(x_data, mean_list, stdev_list, title, xlabel, ylabel, suptitle=None, beverloo=False, beveerloo_flows=None):
    css4, xkcd = get_colors(2)

    plt.errorbar(x_data, mean_list, stdev_list, linestyle='None', marker='o', color=css4[0], label='Promedio')

    if beverloo:
        plt.plot(x_data, beveerloo_flows, linestyle='None', marker='o', color=css4[1], label='Beverloo')
        # ret = np.polyfit(x_data, beveerloo_flows, 1)
        # plt.plot(x_data, np.array(ret[0]) * x_data + ret[1], color=xkcd[1], label='Ajuste modelo lineal')
        # ret = np.polyfit(x_data, mean_list, 1)
        # plt.plot(x_data, np.array(ret[0]) * x_data + ret[1], color=xkcd[0], label='Ajuste modelo lineal')
        plt.legend(loc='best')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.suptitle(suptitle)
    plt.title(title)
    plt.xticks(x_data)

    # aux = [format(n, '.0e') for n in x_data]
    # plt.xticks(x_data, aux)
    #
    # y_ticks = [x / 1000 for x in range(1, 8)]
    # y_ticks.append(1e-4)
    # aux = [format(n, '.0e') for n in y_ticks]
    # plt.yticks(y_ticks, aux)

    plt.tight_layout()
    plt.show()


def mse(x, y, c, e):
    plt.plot(x, y)
    plt.plot(c, e, marker='o', color='red')

    plt.text(c, e, f'({str(c)}, {(format(e, ".0f"))})')

    plt.xlabel('C')
    plt.ylabel('ECM [$(partículas/s)^2$]')
    plt.title('Error del ajuste')

    aux = [n * 1000 for n in range(1, 7)]
    plt.yticks(aux)

    plt.tight_layout()
    plt.show()


def get_colors(n):
    # https://matplotlib.org/3.5.0/tutorials/colors/colors.html
    color_names = {"aqua", "chocolate", "coral", "crimson", "green", "magenta", "navy", "orange", "violet"}
    color_names = {"orange", "blue"}

    if n > len(color_names):
        raise "Plot error: I don't know that many colors"

    css4 = []
    xkcd = []

    for color_name in color_names:
        css4.append(colors.CSS4_COLORS[color_name])
        xkcd.append(colors.XKCD_COLORS[f'xkcd:{color_name}'])

    return css4, xkcd
