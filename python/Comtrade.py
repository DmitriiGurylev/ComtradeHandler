class Comtrade:

    def __init__(self, cfg='', dat=''):
        self._cfg = cfg
        self._dat = dat

    def get_cfg(self):
        return self._cfg

    def get_dat(self):
        return self._dat

    def set_cfg(self, x):
        self._cfg = x

    def set_dat(self, x):
        self._dat = x

