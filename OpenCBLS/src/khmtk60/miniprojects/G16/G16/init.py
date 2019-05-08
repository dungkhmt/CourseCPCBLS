import os
import math
import time
import random
import numpy as np
from tqdm import tqdm
from collections import Counter

from data import Data

class Init():
    def __init__(self, data_dir, init_option, ceof, violation_weight, target_weight):
        self.data = Data(data_dir)
        self.init_option = init_option
        self.ceof = ceof
        self.violation_weight = np.array(violation_weight)
        self.target_weight = np.array(target_weight)
        self.X = np.ones(self.data.nItems, dtype=int)*(-1)
        self.init_funcs = [
            self.init_item_random,
            self.init_item_firstFit,
            self.init_item_firstFit_itemSorted,
            self.init_item_firstFit_binSorted,
            self.init_item_firstFit_binAndItemSorted,
            self.init_bin_random,
            self.init_bin_firstFit,
            self.init_bin_firstFit_binSorted,
            self.init_bin_firstFit_itemSorted,
            self.init_bin_firstFit_binAndItemSorted
        ]
        init_func = self.init_funcs[self.init_option]
        init_func()

    def init_item_random(self, is_show=False):
        for i in range(self.data.nItems):
            self.X[i] = random.choice(self.data.items.binIndices[i])
        
        if is_show:
            print('Init item random solution!\n{}'.format(self.X))

    def init_item_firstFit(self, is_show=False):
        for i in range(self.data.nItems):
            is_packed = False
            for b in self.data.items.binIndices[i]:
                if self.check_bin_for_item(i, b):
                    self.X[i] = b
                    is_packed = True
                    break
            if not is_packed:
                self.X[i] = random.choice(self.data.items.binIndices[i])

        if is_show:
            print('Init item firstfit solution!\n{}'.format(self.X))

    def init_item_firstFit_itemSorted(self, is_show=False):
        itemIdx = {i:len(self.data.items.binIndices[i]) for i in range(self.data.nItems)}
        itemIdx = sorted(itemIdx, key=lambda x:itemIdx[x])
        for i in itemIdx:
            is_packed = False
            for b in self.data.items.binIndices[i]:
                if self.check_bin_for_item(i, b):
                    self.X[i] = b
                    is_packed = True
                    break
            if not is_packed:
                self.X[i] = random.choice(self.data.items.binIndices[i])

        if is_show:
            print('Init item firstfit with sorted items solution!\n{}'.format(self.X))

    def init_item_firstFit_binSorted(self, is_show=False):
        for i in range(self.data.nItems):
            is_packed = False
            binIdx = self.data.items.binIndices[i]
            binIdx = sorted(binIdx, key=lambda x: len(self.data.bins.itemIndices[x]))
            for b in binIdx:
                if self.check_bin_for_item(i, b):
                    self.X[i] = b
                    is_packed = True
                    break
            if not is_packed:
                self.X[i] = random.choice(self.data.items.binIndices[i])

        if is_show:
            print('Init item firstfit with sorted bins solution!\n{}'.format(self.X))

    def init_item_firstFit_binAndItemSorted(self, is_show=False):
        itemIdx = {i:len(self.data.items.binIndices[i]) for i in range(self.data.nItems)}
        itemIdx = sorted(itemIdx, key=lambda x:itemIdx[x])
        for i in itemIdx:
            is_packed = False
            binIdx = self.data.items.binIndices[i]
            binIdx = sorted(binIdx, key=lambda x: len(self.data.bins.itemIndices[x]))
            for b in binIdx:
                if self.check_bin_for_item(i, b):
                    self.X[i] = b
                    is_packed = True
                    break
            if not is_packed:
                self.X[i] = random.choice(self.data.items.binIndices[i])
        if is_show:
            print('Init item firstfit with sorted bins, items solution!\n{}'.format(self.X))
    
    def check_bin_for_item(self, item, b):
        c1 = (self.get_bin_w(b) + self.data.items.w[item] <= self.data.bins.max_w[b])
        c2 = (self.get_bin_p(b) + self.data.items.p[item] <= self.data.bins.max_p[b])

        c3 = False
        bin_nR, bin_R = self.get_bin_nR(b)
        if (self.data.items.r[item] not in bin_R) and (bin_nR<=self.data.bins.max_r[b]-1):
            c3 = True
        if (self.data.items.r[item] in bin_R) and (bin_nR<=self.data.bins.max_r[b]):
            c3 = True

        c4 = False
        bin_nT, bin_T = self.get_bin_nT(b)
        if (self.data.items.t[item] not in bin_T) and (bin_nT<=self.data.bins.max_t[b]-1):
            c4 = True
        if (self.data.items.t[item] in bin_T) and (bin_nT<=self.data.bins.max_t[b]):
            c4 = True
        return (c1 and c2 and c3 and c4)

    def init_bin_random(self, maxIter=20, is_show=False):
        itemPacked = set({})
        it = 0 
        while (len(itemPacked) < self.data.nItems) and (it < maxIter):
            it+=1
            for b in range(self.data.nBins):
                tmp = list(set(self.data.bins.itemIndices[b]) - itemPacked)
                if len(tmp) == 0:
                    continue
                i = random.choice(tmp)
                self.X[i] = b
                itemPacked.add(i)
        for i in set(range(self.data.nItems)) - itemPacked:
            self.X[i] = random.choice(self.data.items.binIndices[i])
        
        if is_show:
            print('Init bin random solution!\n{}'.format(self.X))
    
    def init_bin_firstFit(self, is_show=False):
        itemPacked = set({})
        for b in range(self.data.nBins):
            for i in self.data.bins.itemIndices[b]:
                if (i not in itemPacked) and (self.check_item_for_bin(b, i)):
                    itemPacked.add(i)
                    self.X[i] = b
                
        for i in set(range(self.data.nItems)) - itemPacked:
            self.X[i] = random.choice(self.data.items.binIndices[i])
        
        if is_show:
            print('Init bin firstfit solution!\n{}'.format(self.X))

    def init_bin_firstFit_binSorted(self, maxIter=20, is_show=False):
        itemPacked = set({})
        binIdx = range(self.data.nBins)
        binIdx = sorted(binIdx, key=lambda x: len(self.data.bins.itemIndices[x]))
        for b in binIdx:
            for i in self.data.bins.itemIndices[b]:
                if (i not in itemPacked) and (self.check_item_for_bin(b, i)):
                    itemPacked.add(i)
                    self.X[i] = b
                
        for i in set(range(self.data.nItems)) - itemPacked:
            self.X[i] = random.choice(self.data.items.binIndices[i])

        if is_show:
            print('Init bin firstfit with sorted bins solution!\n{}'.format(self.X))

    def init_bin_firstFit_itemSorted(self, maxIter=20, is_show=False):
        itemPacked = set({})
        binIdx = range(self.data.nBins)
        for b in binIdx:
            itemIdx = self.data.bins.itemIndices[b]
            itemIdx = sorted(itemIdx, key=lambda x: len(self.data.items.binIndices[x]))
            for i in itemIdx:
                if (i not in itemPacked) and (self.check_item_for_bin(b, i)):
                    itemPacked.add(i)
                    self.X[i] = b
                
        for i in set(range(self.data.nItems)) - itemPacked:
            self.X[i] = random.choice(self.data.items.binIndices[i])
        
        if is_show:
            print('Init bin firstfit with sorted items solution!\n{}'.format(self.X))

    def init_bin_firstFit_binAndItemSorted(self, maxIter=20, is_show=False):
        itemPacked = set({})
        binIdx = range(self.data.nBins)
        binIdx = sorted(binIdx, key=lambda x: len(self.data.bins.itemIndices[x]))
        for b in binIdx:
            itemIdx = self.data.bins.itemIndices[b]
            itemIdx = sorted(itemIdx, key=lambda x: len(self.data.items.binIndices[x]))
            for i in itemIdx:
                if (i not in itemPacked) and (self.check_item_for_bin(b, i)):
                    itemPacked.add(i)
                    self.X[i] = b
                
        for i in set(range(self.data.nItems)) - itemPacked:
            self.X[i] = random.choice(self.data.items.binIndices[i])
        
        if is_show:
            print('Init bin firstfit with sorted bins, items solution!\n{}'.format(self.X))

    def check_bin(self, b):
        if self.get_bin_violation_default(b) < self.ceof:
            return True
        return False

    def check_item_for_bin(self, b, item):
        if b not in self.data.items.binIndices[item]:
            return False
        c1 = (self.get_bin_w(b) + self.data.items.w[item] <= self.data.bins.max_w[b])
        c2 = (self.get_bin_p(b) + self.data.items.p[item] <= self.data.bins.max_p[b])

        c3 = False
        bin_nR, bin_R = self.get_bin_nR(b)
        if (self.data.items.r[item] not in bin_R) and (bin_nR<=self.data.bins.max_r[b]-1):
            c3 = True
        if (self.data.items.r[item] in bin_R) and (bin_nR<=self.data.bins.max_r[b]):
            c3 = True

        c4 = False
        bin_nT, bin_T = self.get_bin_nT(b)
        if (self.data.items.t[item] not in bin_T) and (bin_nT<=self.data.bins.max_t[b]-1):
            c4 = True
        if (self.data.items.t[item] in bin_T) and (bin_nT<=self.data.bins.max_t[b]):
            c4 = True

        return (c1 and c2 and c3 and c4)

    def get_bin_w(self, b):
        return np.sum((self.X==b) * self.data.items.w)

    def get_bin_p(self, b):
        return np.sum((self.X==b) * self.data.items.p)

    def get_bin_nR(self, b):
        bin_R = Counter(self.data.items.r[self.X==b])
        return len(bin_R), bin_R

    def get_bin_nT(self, b):
        bin_T = Counter(self.data.items.t[self.X==b])
        return len(bin_T), bin_T
    
    def get_bin_nItems(self, b):
        return np.sum(self.X == b)

    def get_bin_violation_default(self, b):
        mwv = max(0, self.get_bin_w(b) - self.data.bins.max_w[b])
        lwv = max(0, self.data.bins.min_w[b] - self.get_bin_w(b))
        mpv = max(0, self.get_bin_p(b) - self.data.bins.max_p[b])
        mrv = max(0, self.get_bin_nR(b)[0] - self.data.bins.max_r[b])
        mtv = max(0, self.get_bin_nT(b)[0] - self.data.bins.max_t[b])
        return self.funcMulWithWeight(np.array([mwv, lwv, mpv, mrv, mtv]), self.violation_weight)

    def get_violations_default(self):
        sumV = 0
        for b in range(self.data.nBins):
            sumV += self.get_bin_violation_default(b)
        return sumV

    def funcMulWithWeight(self, arr, w):
        return np.sum(arr*w)