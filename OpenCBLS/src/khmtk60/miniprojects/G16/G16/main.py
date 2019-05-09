import os
import re
import time
import json
import random
import numpy as np
import pickle

from localsearch import LocalSearch

if __name__ =='__main__':
    data_dir='./data/MinMaxTypeMultiKnapsackInput-1000.json'
    ceof=1e-9
    violation_weight=np.ones(5)
    target_weight=np.ones(2)

    # violation_weight[1] = 0
    # s = LocalSearch(data_dir=data_dir, init_option=6, ceof=ceof, violation_weight=violation_weight, target_weight=target_weight)
    # s.default_searchTarget_assign(10)
    # tmp = s.X

    maxI = 1
    maxT = 0
    for i in range(10):
        s = LocalSearch(data_dir=data_dir, init_option=i, ceof=ceof, violation_weight=violation_weight, target_weight=target_weight)
        if s.targetOptimize > maxT:
            maxT = s.targetOptimize
            maxI = i
    
    s = LocalSearch(data_dir=data_dir, init_option=1, ceof=ceof, violation_weight=violation_weight, target_weight=target_weight)
    t1 = time.time()
    s.custom_searchTarget_assign(10000)

    if os.path.exists('result-target-{}.pickle'.format(s.data.nItems)):
        fname = 'result-target-{}.pickle'.format(s.data.nItems)
    else:
        fname = 'result-violation-{}.pickle'.format(s.data.nItems)
    with open(fname, 'rb') as f:
        data = pickle.load(f)
        best_solution = data['solution']
    result = {}
    nItemsCorrect, itemsCorrect = best_solution.get_items_correct()
    nBinsCorrect, binsCorrect = best_solution.get_bins_correct()

    print(nItemsCorrect, nBinsCorrect)
    
    for i in range(best_solution.data.nItems):
        if i in itemsCorrect:
            result['{}'.format(i)] = int(best_solution.X[i])
        else:
            result['{}'.format(i)] = -1
    print(result)
    
    with open('result-{}.json'.format(best_solution.data.nItems), 'w') as f:
        json.dump(result, f)

    print((time.time() - t1))
