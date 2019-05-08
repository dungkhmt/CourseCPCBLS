import os
import re
import time
import json
import pickle
import random
import numpy as np

class Items():
    def __init__(self):
        self.w = []
        self.p = []
        self.r = []
        self.t = []
        self.binIndices = dict()

class Bins():
    def __init__(self):
        self.max_w = []
        self.min_w = []
        self.max_p = []
        self.max_r = []
        self.max_t = []
        self.itemIndices = dict()

class Data():
    def __init__(self, jDataFilePath, n_round=3):
        self.jDataFilePath = jDataFilePath
        self.pDataFilePath = '.'.join(re.split('[.]', jDataFilePath)[:-1] + ['pickle'])
        self.n_round = n_round
        self.items = Items()
        self.bins = Bins()
        self.itemIsSwapIndices = {}

        self.nItems = 0
        self.nBins = 0
        self.nR = 0
        self.nT = 0

        t_start = time.time()
        if os.path.isfile(self.pDataFilePath):
            self.read_data_from_pickle()
            print('read data from pickle file: nItems {} nBins {} nR {} nT {} in {}s'.format(self.nItems, self.nBins, self.nR, self.nT, time.time()-t_start))
        else:
            self.read_data_from_json()
            self.save_data_to_pickle()
            print('read data from json file: nItems {} nBins {} nR {} nT {} in {}s'.format(self.nItems, self.nBins, self.nR, self.nT, time.time()-t_start))

    def save_data_to_pickle(self):
        with open(self.pDataFilePath, 'wb') as dt:
            pickle.dump(self, dt, protocol=pickle.HIGHEST_PROTOCOL)

    def read_data_from_pickle(self):
        with open(self.pDataFilePath, 'rb') as dt:
            data = pickle.load(dt)
        self.items = data.items
        self.bins = data.bins
        self.itemIsSwapIndices = data.itemIsSwapIndices
        self.nItems = data.nItems
        self.nBins = data.nBins
        self.nR = data.nR
        self.nT = data.nT

    def read_data_from_json(self):
        with open(self.jDataFilePath) as f:
            data = json.load(f)

        for idx, item in enumerate(data['items']):
            self.items.w.append(round(item['w'], self.n_round))
            self.items.p.append(round(item['p'], self.n_round))
            self.items.r.append(item['r'])
            self.items.t.append(item['t'])
            self.items.binIndices[idx] = np.array(item['binIndices'])

        for idx, b in enumerate(data['bins']):
            self.bins.max_w.append(round(b['capacity'], self.n_round))
            self.bins.min_w.append(round(b['minLoad'], self.n_round))
            self.bins.max_p.append(round(b['p'], self.n_round))
            self.bins.max_r.append(b['r'])
            self.bins.max_t.append(b['t'])

        self.close()

    def close(self):
        self.items.w = np.array(self.items.w)
        self.items.p = np.array(self.items.p)
        self.items.r = np.array(self.items.r)
        self.items.t = np.array(self.items.t)
        self.nItems = len(self.items.w)

        self.bins.max_w = np.array(self.bins.max_w)
        self.bins.min_w = np.array(self.bins.min_w)
        self.bins.max_p = np.array(self.bins.max_p)
        self.bins.max_r = np.array(self.bins.max_r)
        self.bins.max_t = np.array(self.bins.max_t)
        self.nBins = len(self.bins.max_w)

        for i in range(self.nBins):
            self.bins.itemIndices[i] = set()

        r2index = {r: i for i,r in enumerate(set(self.items.r))}
        self.nR = len(r2index)
        t2index = {t: i for i,t in enumerate(set(self.items.t))}
        self.nT = len(t2index)
        for i in range(self.nItems):
            self.items.r[i] = r2index[self.items.r[i]]
            self.items.t[i] = t2index[self.items.t[i]]
            for b in self.items.binIndices[i]:
                self.bins.itemIndices[b].add(i)
        
        for i in range(self.nBins):
            self.bins.itemIndices[i] = np.array(list(self.bins.itemIndices[i]))

        for i in range(self.nItems):
            self.itemIsSwapIndices[i] = {}
            for b in self.items.binIndices[i]:
                for j in set(self.bins.itemIndices[b]) - set({i}):
                    self.itemIsSwapIndices[i][j] = True


