from matplotlib import pyplot as plt, colors
import numpy as np
from scipy.stats import norm


def histogram_and_pdf(data, bins_limits, best_c=None, density=True):
    # https://levelup.gitconnected.com/probability-distributions-using-scipy-58fdab53d7ac

    if density and best_c is None:
        raise 'Error'

    # plt.bar(aux, data, align='edge', width=bin_width, edgecolor="black")

    # density --> yi = Ni / (dxi * N)
    a1, a2, a3 = plt.hist(data, bins=bins_limits, edgecolor="black", density=density, label='Datos', align='mid')    # data should have particles final positions

    # d = [b for b in bins_limits]
    # plt.hist(d, bins=bins_limits, color='r',width=5.4, alpha=0.5, edgecolor="black")    # data should have positions

    pdf = []

    if density:
        # ajuste a distribucion gaussiana
        t = np.linspace(-60, 60, num=100)  # generates equally num spaced numbers between start and stop
        pdf = norm.pdf(t, np.mean(data), best_c)
        plt.plot(t, pdf, color='r', label='Ajuste')

        plt.title('Densidad de partículas por bin y ajuste a distribución gaussiana')
        plt.ylabel('Densidad')
        plt.legend()
    else:
        plt.title('Número de partículas por bin')
        plt.ylabel('y [partículas]')

    plt.xlabel('x [cm]')

    aux = [n - 60 for n in range(0, 130, 10)]
    plt.xticks(aux)

    plt.suptitle(f'N = {str(len(data))}')
    plt.tight_layout()
    plt.show()

    return a1, pdf


def error_vs_n(Ns, squared_error_list):
    plt.plot(Ns, squared_error_list, linestyle='None', marker='o')

    plt.xlabel('N')
    plt.ylabel('Error')
    plt.title(f'Error del ajuste vs N')
    plt.xticks(Ns)

    plt.yscale("log")

    aux = [2.4, 2.2, 2.0, 1.8, 1.6, 1.4, 1.2, 1.0]
    aux = [a*1e-4 for a in aux]
    aux.append(9e-5)
    plt.yticks(aux)

    plt.tight_layout()
    plt.show()


def mean_and_std(x_data, mean_list, std_list, title, xlabel, ylabel, suptitle=None):
    plt.errorbar(x_data, mean_list, std_list, linestyle='None', marker='o', label='Promedio')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    plt.suptitle(suptitle)
    plt.title(title)
    plt.xticks(x_data)

    plt.tight_layout()
    plt.show()


def c_plot(x, y, c, e, N):
    plt.plot(x, y)
    plt.plot(c, e, marker='o', color='red')

    plt.text(c, e, f'({str(c)}, {(format(e, ".2e"))})')

    plt.xlabel('C')
    plt.ylabel('E(c)')
    plt.title('Error en función de C')
    plt.suptitle(f'N = {N}')

    plt.yscale("log")

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
