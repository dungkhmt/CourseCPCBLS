import os
import math
import time
import random
import numpy as np
from tqdm import tqdm
from collections import Counter

from init import Init

class Solution(Init):
    def __init__(self, data_dir, init_option, ceof, violation_weight, target_weight):
        super(Solution, self).__init__(data_dir, init_option, ceof, violation_weight, target_weight)
        self.bins_inf = {}
        for i in range(self.data.nBins):
            self.bins_inf[i] = self.get_bin_inf(i)
            self.bins_inf[i].update({'bin_violation': self.get_bin_violation_default(i)})
            self.bins_inf[i].update({'nItems': self.get_bin_nItems(i)})
        self.violations = self.get_violations()
        self.nBinsCorrect = self.get_bins_correct()[0]
        self.nItemsCorrect = self.get_items_correct()[0]
        self.targetOptimize = self.funcMulWithWeight(np.array([self.nBinsCorrect, self.nItemsCorrect]), self.target_weight)
        
        print('=========================================================')
        print('Init number bins correct: {}/{} bins'.format(self.nBinsCorrect, self.data.nBins))
        print('Init number items correct: {}/{} items'.format(self.nItemsCorrect, self.data.nItems))
        print('Init violation : {}'.format(self.violations))
        print('Init target optimize: {}'.format(self.targetOptimize))
        print('=========================================================')

    def is_stop(self):
        if abs(self.violations) < self.ceof:
            print('Done Search!')
            print()
            self.print_result()
            return True
        return False
    
    def print_result(self):
        for i in range(self.data.nBins):
            print('Bin {} with w:{}/{} p:{}/{} nR:{}/{} nT:{}/{}'.format(i, self.get_bin_w(i), self.data.bins.max_w[i], self.get_bin_p(i), \
                self.data.bins.max_p[i], self.get_bin_nR(i)[0], self.data.bins.max_r[i], self.get_bin_nT(i)[0], self.data.bins.max_t[i]))
            if abs(self.bins_inf[i]['bin_violation']) < self.ceof:
                print('Solution: {}'.format(np.argwhere(self.X==i).ravel()))
            print('---------------------------------------------------------')
            

    def get_locationMostViolation_assign(self):
        maxV = 0
        tmp = []
        for i in range(self.data.nItems):
            for b in self.data.items.binIndices[i]:
                move_v = self.get_violation_assign(i, b)
                if move_v['violation_decrease'] < maxV:
                    maxV = move_v['violation_decrease']
                    tmp = []
                    tmp.append((i, b, move_v))
                elif abs(move_v['violation_decrease'] - maxV) < self.ceof :
                    tmp.append((i, b, move_v))
        return maxV, tmp

    def get_locationMostTarget_assign(self):
        maxTarget = 0
        tmp = []
        for i in range(self.data.nItems):
            for b in self.data.items.binIndices[i]:
                move_v = self.get_violation_assign(i, b)
                if move_v['target_increase'] > maxTarget:
                    maxTarget = move_v['target_increase']
                    tmp = []
                    tmp.append((i, b, move_v))
                elif abs(move_v['target_increase'] - maxTarget) < self.ceof :
                    tmp.append((i, b, move_v))
        return maxTarget, tmp

    def get_locationMostViolation_swap(self):
        maxV = self.violations
        tmp = []
        for i in range(self.data.nItems):
            for j in self.data.itemIsSwapIndices[i]:
                if i > j:
                    continue
                move_v = self.get_violation_swap(i, j)
                if move_v['violation_decrease'] < maxV:
                    maxV = move_v['violation_decrease']
                    tmp = []
                    tmp.append((i, j, move_v))
                elif abs(move_v['violation_decrease'] - maxV) < self.ceof:
                    tmp.append((i, j, move_v))
        return maxV, tmp

    def get_locationMostTarget_swap(self):
        maxTarget = 0
        tmp = []
        for i in range(self.data.nItems):
            for b in self.data.itemIsSwapIndices[i]:
                move_v = self.get_violation_swap(i, b)
                if move_v['target_increase'] > maxTarget:
                    maxTarget = move_v['target_increase']
                    tmp = []
                    tmp.append((i, b, move_v))
                elif abs(move_v['target_increase'] - maxTarget) < self.ceof :
                    tmp.append((i, b, move_v))
        return maxTarget, tmp

    def get_violation_assign(self, idx, newvalue):
        oldvalue = self.X[idx]
        if self.X[idx] == newvalue:
            return {
                'nBins_increase': 0,
                'nItems_increase': 0,
                'target_increase': 0,
                'violation_decrease': 0,
                oldvalue: self.bins_inf[oldvalue],
                newvalue: self.bins_inf[newvalue]
            }
        old_inf = self.get_bin_inf_assign(oldvalue, idx, isAddItem=False)
        old_inf['bin_violation'] = self.cal_bin_violation(oldvalue, old_inf)

        new_inf = self.get_bin_inf_assign(newvalue, idx, isAddItem=True)
        new_inf['bin_violation'] = self.cal_bin_violation(newvalue, new_inf)

        old_is_true = (abs(old_inf['bin_violation']) < self.ceof)
        old_check = (abs(self.bins_inf[oldvalue]['bin_violation']) < self.ceof)
        old_nBins_increase = (old_is_true*1 - old_check*1)
        old_nItems_increase = old_is_true*old_inf['nItems'] - old_check*self.bins_inf[oldvalue]['nItems']
        old_target_increase = self.funcMulWithWeight(np.array([old_nBins_increase, old_nItems_increase]), self.target_weight)
        old_violation_decrease = old_inf['bin_violation'] - self.bins_inf[oldvalue]['bin_violation']
        
        new_is_true = (abs(new_inf['bin_violation']) < self.ceof)
        new_check = (abs(self.bins_inf[newvalue]['bin_violation']) < self.ceof)
        new_nBins_increase = (new_is_true*1 - new_check*1)
        new_nItems_increase = new_is_true*new_inf['nItems'] - new_check*self.bins_inf[newvalue]['nItems']
        new_target_increase = self.funcMulWithWeight(np.array([new_nBins_increase, new_nItems_increase]), self.target_weight)
        new_violation_decrease = new_inf['bin_violation'] - self.bins_inf[newvalue]['bin_violation']
        
        return {
            'nBins_increase': new_nBins_increase + old_nBins_increase,
            'nItems_increase': new_nItems_increase + old_nItems_increase,
            'target_increase': new_target_increase + old_target_increase,
            'violation_decrease': old_violation_decrease + new_violation_decrease,
            oldvalue: old_inf,
            newvalue: new_inf
        }

    def cal_bin_violation(self, b, bin_inf):
        mwv = max(0, bin_inf['bin_w'] - self.data.bins.max_w[b])
        lwv = max(0, self.data.bins.min_w[b] - bin_inf['bin_w'])
        mpv = max(0, bin_inf['bin_p'] - self.data.bins.max_p[b])
        mrv = max(0, bin_inf['bin_nR'] - self.data.bins.max_r[b])
        mtv = max(0, bin_inf['bin_nT'] - self.data.bins.max_t[b])
        return self.funcMulWithWeight(np.array([mwv, lwv, mpv, mrv, mtv]), self.violation_weight)

    def get_bin_inf_assign(self, b, i, isAddItem=True):
        if isAddItem:
            bin_R = self.bins_inf[b]['bin_R'] + Counter({self.data.items.r[i]: 1})
            bin_T = self.bins_inf[b]['bin_T'] + Counter({self.data.items.t[i]: 1})
            return {
                'bin_w': self.bins_inf[b]['bin_w'] + self.data.items.w[i],
                'bin_p': self.bins_inf[b]['bin_p'] + self.data.items.p[i],
                'bin_R': bin_R,
                'bin_nR': len(bin_R),
                'bin_T': bin_T,
                'bin_nT': len(bin_T),
                'nItems': self.bins_inf[b]['nItems'] + 1,
                'bin_violation': self.bins_inf[b]['bin_violation']
            }
        else:
            bin_R = self.bins_inf[b]['bin_R'] - Counter({self.data.items.r[i]: 1})
            bin_T = self.bins_inf[b]['bin_T'] - Counter({self.data.items.t[i]: 1})
            return {
                'bin_w': self.bins_inf[b]['bin_w'] - self.data.items.w[i],
                'bin_p': self.bins_inf[b]['bin_p'] - self.data.items.p[i],
                'bin_R': bin_R,
                'bin_nR': len(bin_R),
                'bin_T': bin_T,
                'bin_nT': len(bin_T),
                'nItems': self.bins_inf[b]['nItems'] - 1,
                'bin_violation': self.bins_inf[b]['bin_violation']
            }

    def get_violation_swap(self, idx1, idx2):
        oldvalue = self.X[idx1]
        newvalue = self.X[idx2]
        if oldvalue == newvalue:
            return {
                'nBins_increase': 0,
                'nItems_increase': 0,
                'target_increase': 0,
                'violation_decrease': 0,
                oldvalue: self.bins_inf[oldvalue],
                newvalue: self.bins_inf[newvalue]
            }

        infs = self.get_bin_inf_swap(idx1, idx2)
        old_inf = infs[oldvalue]
        old_inf['bin_violation'] = self.cal_bin_violation(oldvalue, old_inf)

        new_inf = infs[newvalue]
        new_inf['bin_violation'] = self.cal_bin_violation(newvalue, new_inf)

        old_is_true = (abs(old_inf['bin_violation']) < self.ceof)
        old_check = (abs(self.bins_inf[oldvalue]['bin_violation']) < self.ceof)
        old_nBins_increase = (old_is_true*1 - old_check*1)
        old_nItems_increase = old_is_true*old_inf['nItems'] - old_check*self.bins_inf[oldvalue]['nItems']
        old_target_increase = self.funcMulWithWeight(np.array([old_nBins_increase, old_nItems_increase]), self.target_weight)
        old_violation_decrease = old_inf['bin_violation'] - self.bins_inf[oldvalue]['bin_violation']
        
        new_is_true = (abs(new_inf['bin_violation']) < self.ceof)
        new_check = (abs(self.bins_inf[newvalue]['bin_violation']) < self.ceof)
        new_nBins_increase = (new_is_true*1 - new_check*1)
        new_nItems_increase = new_is_true*new_inf['nItems'] - new_check*self.bins_inf[newvalue]['nItems']
        new_target_increase = self.funcMulWithWeight(np.array([new_nBins_increase, new_nItems_increase]), self.target_weight)
        new_violation_decrease = new_inf['bin_violation'] - self.bins_inf[newvalue]['bin_violation']

        return {
            'nBins_increase': new_nBins_increase + old_nBins_increase,
            'nItems_increase': new_nItems_increase + old_nItems_increase,
            'target_increase': new_target_increase + old_target_increase,
            'violation_decrease': old_violation_decrease + new_violation_decrease,
            oldvalue: old_inf,
            newvalue: new_inf
        }

    def get_bin_inf_swap(self, idx1, idx2):
        oldvalue = self.X[idx1]
        newvalue = self.X[idx2]
        oldBin_R = self.bins_inf[oldvalue]['bin_R'] + Counter({self.data.items.r[idx2]: 1}) - Counter({self.data.items.r[idx1]: 1})
        oldBin_T = self.bins_inf[oldvalue]['bin_T'] + Counter({self.data.items.t[idx2]: 1}) - Counter({self.data.items.t[idx1]: 1})

        newBin_R = self.bins_inf[newvalue]['bin_R'] + Counter({self.data.items.r[idx1]: 1}) - Counter({self.data.items.r[idx2]: 1})
        newBin_T = self.bins_inf[newvalue]['bin_T'] + Counter({self.data.items.t[idx1]: 1}) - Counter({self.data.items.t[idx2]: 1})
        return {
            oldvalue : {
                'bin_w': self.bins_inf[oldvalue]['bin_w'] + self.data.items.w[idx2] - self.data.items.w[idx1],
                'bin_p': self.bins_inf[oldvalue]['bin_p'] + self.data.items.p[idx2] - self.data.items.w[idx1],
                'bin_R': oldBin_R,
                'bin_nR': len(oldBin_R),
                'bin_T': oldBin_T,
                'bin_nT': len(oldBin_T),
                'nItems': self.bins_inf[oldvalue]['nItems'],
                'bin_violation': self.bins_inf[oldvalue]['bin_violation']
            },
            newvalue: {
                'bin_w': self.bins_inf[newvalue]['bin_w'] + self.data.items.w[idx1] - self.data.items.w[idx2],
                'bin_p': self.bins_inf[newvalue]['bin_p'] + self.data.items.p[idx1] - self.data.items.w[idx2],
                'bin_R': newBin_R,
                'bin_nR': len(newBin_R),
                'bin_T': newBin_T,
                'bin_nT': len(newBin_T),
                'nItems': self.bins_inf[newvalue]['nItems'],
                'bin_violation': self.bins_inf[newvalue]['bin_violation']
            }
        }

    def set_newSolution_assign(self, idx, newvalue, result):
        oldvalue = self.X[idx]
        self.X[idx] = newvalue
        self.bins_inf[oldvalue] = result[oldvalue]
        self.bins_inf[newvalue] = result[newvalue]
        self.violations += result['violation_decrease']
        self.targetOptimize += result['target_increase']
        self.nBinsCorrect += result['nBins_increase']
        self.nItemsCorrect += result['nItems_increase']

    def set_newSolution_swap(self, idx1, idx2, result):
        oldvalue = self.X[idx1]
        newvalue = self.X[idx2]
        self.X[idx1] = newvalue
        self.X[idx2] = oldvalue
        self.bins_inf[oldvalue] = result[oldvalue]
        self.bins_inf[newvalue] = result[newvalue]
        self.violations += result['violation_decrease']
        self.targetOptimize += result['target_increase']
        self.nBinsCorrect += result['nBins_increase']
        self.nItemsCorrect += result['nItems_increase']

    def get_bin_inf(self, b):
        return {
            'bin_w': self.get_bin_w(b),
            'bin_p': self.get_bin_p(b),
            'bin_nR': self.get_bin_nR(b)[0],
            'bin_R': self.get_bin_nR(b)[1],
            'bin_nT': self.get_bin_nT(b)[0],
            'bin_T': self.get_bin_nT(b)[1]
        }

    def get_bin_violation(self, b):
        return self.bins_inf[b]['bin_violation']

    def get_violations(self):
        return sum([self.bins_inf[i]['bin_violation'] for i in range(self.data.nBins)])

    def get_bins_correct(self):
        nBins = 0
        binIdx = []
        for b in range(self.data.nBins):
            if abs(self.bins_inf[b]['bin_violation']) < self.ceof:
                nBins +=1
                binIdx.append(b)
        return nBins, binIdx

    def get_items_correct(self):
        itemIdx = []
        for b in range(self.data.nBins):
            if abs(self.bins_inf[b]['bin_violation']) < self.ceof:
                itemIdx.extend(np.argwhere(self.X==b).ravel())
        itemIdx = list(set(itemIdx))
        return len(itemIdx), itemIdx

    def set_newSolution(self, newSolution):
        self.X = newSolution
        self.bins_inf = {}
        for i in range(self.data.nBins):
            self.bins_inf[i] = self.get_bin_inf(i)
            self.bins_inf[i].update({'bin_violation': self.get_bin_violation_default(i)})
            self.bins_inf[i].update({'nItems': self.get_bin_nItems(i)})
        self.violations = self.get_violations()
        self.nBinsCorrect = self.get_bins_correct()[0]
        self.nItemsCorrect = self.get_items_correct()[0]
        self.targetOptimize = self.funcMulWithWeight(np.array([self.nBinsCorrect, self.nItemsCorrect]), self.target_weight)
        print('=========================================================')
        print('Set new Solution number bins correct: {}/{} bins'.format(self.nBinsCorrect, self.data.nBins))
        print('Set new Solution number items correct: {}/{} items'.format(self.nItemsCorrect, self.data.nItems))
        print('Set new Solution violation : {}'.format(self.violations))
        print('Set new Solution target optimize: {}'.format(self.targetOptimize))
        print('=========================================================')