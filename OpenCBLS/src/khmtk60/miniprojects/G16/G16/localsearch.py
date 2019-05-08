import os
import pickle
import random
import copy
import numpy as np
from solution import Solution

class LocalSearch(Solution):
    def __init__(self, data_dir, init_option, ceof, violation_weight, target_weight):
        super(LocalSearch, self).__init__(data_dir, init_option, ceof, violation_weight, target_weight)

    def default_searchViolation_assign(self, maxIter, kmax=10):
        self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems))
        maxV = self.violations
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems), searchTarget=False)
                break
            result = self.get_locationMostViolation_assign()
            if result[0] < 0 and len(result[1]) > 0 and abs(result[0]) > self.ceof:
                k = 0
                r = random.choice(result[1])
                self.set_newSolution_assign(r[0], r[1], r[2])
                if self.violations < maxV:
                    maxV = self.violations
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems), searchTarget=False)
            k += 1
            if k > kmax:
                self.restart_init()
    
    def default_searchTarget_assign(self, maxIter, kmax=10):
        self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
        maxT = self.targetOptimize
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
                break
            result = self.get_locationMostTarget_assign()
            if result[0] > 0 and len(result[1]) > 0 and abs(result[0]) > self.ceof:
                k = 0
                r = random.choice(result[1])
                self.set_newSolution_assign(r[0], r[1], r[2])
                if self.targetOptimize > maxT:
                    maxT = self.targetOptimize
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
            k += 1
            if k > kmax:
                self.restart_init()

    def default_searchViolation_swap(self, maxIter, kmax=10):
        self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems))
        maxV = self.violations
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems), searchTarget=False)
                break
            result = self.get_locationMostViolation_swap()
            
            if result[0] < 0 and len(result[1]) > 0 and abs(result[0]) > self.ceof:
                k = 0
                r = random.choice(result[1])
                self.set_newSolution_swap(r[0], r[1], r[2])
                if self.violations < maxV:
                    maxV = self.violations
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-violation-{}.pickle'.format(self.data.nItems), searchTarget=False)
            k+= 1
            if k > kmax:
                self.restart_init()

    def default_searchTarget_swap(self, maxIter, kmax=10):
        self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
        maxT = self.targetOptimize
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
                break
            result = self.get_locationMostTarget_swap()
            if result[0] > 0 and len(result[1]) > 0:
                k = 0
                r = random.choice(result[1])
                self.set_newSolution_swap(r[0], r[1], r[2])
                if self.targetOptimize > maxT:
                    maxT = self.targetOptimize
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
            k+= 1
            if k > kmax:
                self.restart_init()

    def custom_searchTarget_assign(self, maxIter, tabulen=50, sTable=10):
        self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
        maxT = self.targetOptimize
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        tabu = {}
        for i in range(self.data.nItems):
            tabu[i] = {}
            for b in self.data.items.binIndices[i]:
                tabu[i][b] = -1

        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
                break
            maxTarget = 0
            tmp = []
            for i in range(self.data.nItems):
                for b in self.data.items.binIndices[i]:
                    if tabu[i][b] <= it:
                        move_v = self.get_violation_assign(i, b)
                        if move_v['target_increase'] > maxTarget:
                            maxTarget = move_v['target_increase']
                            tmp = []
                            tmp.append((i, b, move_v))
                        elif abs(move_v['target_increase'] - maxTarget) < self.ceof :
                            tmp.append((i, b, move_v))
            if maxTarget > 0 and len(tmp) > 0 and abs(maxTarget) > self.ceof:
                k = 0
                r = random.choice(tmp)
                tabu[r[0]][r[1]] = it+tabulen
                self.set_newSolution_assign(r[0], r[1], r[2])
                if self.targetOptimize > maxT:
                    maxT = self.targetOptimize
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
            k += 1
            if k > sTable:
                self.restart_init()

    def custom_searchTarget_swap(self, maxIter, tabulen=50, sTable=10):
        self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
        maxT = self.targetOptimize
        mTarget = self.funcMulWithWeight(np.array([self.data.nBins, self.data.nItems]), self.target_weight)
        best_ = copy.copy(self)
        k = 0
        tabu = {}
        for i in range(self.data.nItems):
            tabu[i] = {}
            for b in self.data.itemIsSwapIndices[i]:
                tabu[i][b] = -1

        for it in range(maxIter):
            print('Iter {}:'.format(it))
            print('\t Current Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(self.violations, self.nBinsCorrect, self.data.nBins, \
                self.nItemsCorrect, self.data.nItems, self.targetOptimize, mTarget))
            print('\t Best Solution: cViolation: {} nBinsCorrect: {}/{} nItemsCorrect: {}/{} Target: {}/{}'.format(best_.violations, best_.nBinsCorrect, best_.data.nBins, \
                best_.nItemsCorrect, best_.data.nItems, best_.targetOptimize, mTarget))
            if self.is_stop():
                self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
                break
            maxTarget = 0
            tmp = []
            for i in range(self.data.nItems):
                for b in self.data.itemIsSwapIndices[i]:
                    if tabu[i][b] <= it:
                        move_v = self.get_violation_swap(i, b)
                        if move_v['target_increase'] > maxTarget:
                            maxTarget = move_v['target_increase']
                            tmp = []
                            tmp.append((i, b, move_v))
                        elif abs(move_v['target_increase'] - maxTarget) < self.ceof :
                            tmp.append((i, b, move_v))
            if maxTarget > 0 and len(tmp) > 0 and abs(maxTarget) > self.ceof:
                k = 0
                r = random.choice(tmp)
                tabu[r[0]][r[1]] = it+tabulen
                self.set_newSolution_swap(r[0], r[1], r[2])
                if self.targetOptimize > maxT:
                    maxT = self.targetOptimize
                    best_ = copy.copy(self)
                    self.save_result_to_pickle('result-target-{}.pickle'.format(self.data.nItems))
            k += 1
            if k > sTable:
                self.restart_init()

    def restart_init(self, init_option=None):
        if init_option is None or init_option >= len(self.init_funcs) or init_option < 0:
            init_option = random.randint(0, len(self.init_funcs)-1)
        print('Restart init solution with option {}!'.format(init_option))
        init_func = self.init_funcs[init_option]
        init_func()
        for i in range(self.data.nBins):
            self.bins_inf[i] = self.get_bin_inf(i)
            self.bins_inf[i].update({'bin_violation': self.get_bin_violation_default(i)})
            self.bins_inf[i].update({'nItems': self.get_bin_nItems(i)})
        self.violations = self.get_violations()
        self.nBinsCorrect = self.get_bins_correct()[0]
        self.nItemsCorrect = self.get_items_correct()[0]
        self.targetOptimize = self.funcMulWithWeight(np.array([self.nBinsCorrect, self.nItemsCorrect]), self.target_weight)

    def save_result_to_pickle(self, filename, searchTarget=True):
        if searchTarget:
            maxT = 0
            if os.path.exists('filename'):
                with open(filename, 'rb') as dt:
                    data = pickle.load(dt)
                    maxT = data['maxT']

            if self.targetOptimize > maxT:
                with open(filename, 'wb') as dt:
                    pickle.dump({'maxT': self.targetOptimize, 'maxV': self.violations, 'solution': self}, dt, protocol=pickle.HIGHEST_PROTOCOL)
        else:
            maxV = 0
            if os.path.exists('filename'):
                with open(filename, 'rb') as dt:
                    data = pickle.load(dt)
                    maxV = data['max']

            if self.violations < maxV:
                with open(filename, 'wb') as dt:
                    pickle.dump({'maxV': self.violations, 'maxV': self.violations, 'solution': self}, dt, protocol=pickle.HIGHEST_PROTOCOL)