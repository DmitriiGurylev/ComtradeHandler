from Comtrade import Comtrade


def build_cfg_signal_row(signal_array):
    cfg_row = ''
    for i in range(signal_array):
        cfg_row = cfg_row + signal_array[i]
        if i != len(signal_array):
            cfg_row = cfg_row + ','
    return cfg_row


def transform_primary_values_to_secondary(comtrade, array_of_analog_signals, primary_nominal, secondary_nominal):
    comtrade_new = Comtrade()

    cfg = comtrade.get_cfg().split('\n')
    number_of_signals = cfg[1].split(',')
    analog_signals = number_of_signals[0].replace('[^0-9]', '')
    dat_rows = comtrade.get_dat().split('\n')
    transform_factor = primary_nominal / secondary_nominal
    for i in range(dat_rows):
        fields_of_row = dat_rows[i].split(',')
        for j in range(analog_signals):
            if array_of_analog_signals is None or (j + 1) in array_of_analog_signals:
                fields_of_row[2 + j] = fields_of_row[2 + j] / transform_factor
        sb = ''
        for j in range(fields_of_row):
            sb = sb + fields_of_row[j]
            if j != len(fields_of_row) - 1:
                sb = sb + ','
        dat_rows[i] = sb

    sb = ''
    for i in range(dat_rows):
        sb = sb + dat_rows[i]
        if i != len(dat_rows) - 1:
            sb = sb + '\n'

    comtrade_new.set_cfg(comtrade.get_cfg())
    comtrade_new.set_dat(sb)
    return comtrade_new


def add_digit_to_name_of_signal(phase_a, phase_b, phase_c, comtrade_number,
                                signal_number_1, signal_number_2, signal_number_3):
    signal_a = phase_a.split(',')
    signal_b = phase_b.split(',')
    signal_c = phase_c.split(',')
    if comtrade_number != '1':
        signal_a[0] = signal_number_1
        signal_b[0] = signal_number_2
        signal_c[0] = signal_number_3
    signal_a[1] = signal_a[1] + comtrade_number
    signal_b[1] = signal_b[1] + comtrade_number
    signal_c[1] = signal_c[1] + comtrade_number
    return [signal_a, signal_b, signal_c]


def fill_row_of_dat_file(column):
    sb = ''
    for i in range(column):
        sb = sb + column[i]
        if i != len(column) - 1:
            sb = sb + ','
        else:
            sb = sb + '\n'
    return sb


def arrayCopy(src, srcPos, dest, destPos, length):
    for i in range(length):
        dest[i + destPos] = src[i + srcPos]


def build_cfg(comtrades):
    cfg = comtrades[0].get_cfg().split('\n')
    number_of_additional_rows_in_cfg = (len(comtrades) - 1) * 3
    cfg_new = []
    cfg_new[0] = cfg[0]
    number_of_signals = cfg[1].split(',')
    number_of_signals[0] = number_of_signals[0] + number_of_additional_rows_in_cfg
    number_of_signals[1] = number_of_signals[1].replace('[^0-9]', '') + number_of_additional_rows_in_cfg
    cfg_new[1] = number_of_signals[0] + ',' + number_of_signals[1] + 'A,' + number_of_signals[2]

    column_position = 2
    for i in range(comtrades):
        cfg = comtrades[i].get_cfg().split('\n')
        signals = add_digit_to_name_of_signal(cfg[2], cfg[3], cfg[4], (i + 1), (i * 3 + 1), (i * 3 + 2), (i * 3 + 3))
        cfg_new[column_position] = build_cfg_signal_row(signals[0])
        column_position = column_position + 1
        cfg_new[column_position] = build_cfg_signal_row(signals[1])
        column_position = column_position + 1
        cfg_new[column_position] = build_cfg_signal_row(signals[2])
        column_position = column_position + 1
    arrayCopy(cfg, 5, cfg_new, len(comtrades)-2*3+8, 8)
    sb = ''
    for j in cfg_new:
        sb = sb + j + '\n'
    return sb


def build_dat
