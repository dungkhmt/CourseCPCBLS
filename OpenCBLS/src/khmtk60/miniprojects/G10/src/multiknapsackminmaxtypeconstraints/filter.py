import json
from pprint import pprint
import numpy as np
from matplotlib.pyplot import *
from collections import Counter

path = 'data/MinMaxTypeMultiKnapsackInput-1000.json'

def read_data(path):
    with open(path) as f:
      data = json.load(f)

    w = [item['w'] for item in data['items']]
    p = [item['p'] for item in data['items']]
    t = [item['t'] for item in data['items']]
    r = [item['r'] for item in data['items']]
    D = [item['binIndices'] for item in data['items']]
    N = len(w)
    NT = max(t)+1
    NR = max(r)+1

    LW = [pack['minLoad'] for pack in data['bins']]
    W = [pack['capacity'] for pack in data['bins']]
    P = [pack['p'] for pack in data['bins']]
    T = [pack['t'] for pack in data['bins']]
    R = [pack['r'] for pack in data['bins']]
    M = len(W)

    w = np.array(w)
    p = np.array(p)
    t = np.array(t)
    r = np.array(r)
    LW = np.array(LW)
    W = np.array(W)
    P = np.array(P)
    T = np.array(T)
    R = np.array(R)

    return N,w,p,t,r,D, NT,NR, M,LW,W,P,T,R

def filter_by_r(N,w,r,D, M,LW,R):
    if not np.all(R == 1): return D
    
    # group item by r
    r_id = np.unique(sorted(r))
    r_item = [np.where(r==idx)[0] for idx in r_id]
    r_w = np.array([sum(w[item]) for item in r_item])
   
    # remove item
    remove_r = np.where(r_w < min(LW))[0]
    remove_item = []
    for idx in remove_r:
        remove_item.extend(r_item[idx])

    D1 = D.copy()
    for item in remove_item:
        D1[item] = []

    print('Num remove_r = %d' % len(remove_r), r_id[remove_r])
    print('Num remove_item = %d' % len(remove_item))
    return D1, remove_item

def filter_by_bin(N,w,t,r,D, M,LW,W,T,R):

    # filter bin W <= LW or W <= 0
    remove_bin_dummy0 = set(np.where(W <= LW)[0])
    remove_bin_dummy1 = set(np.where(W <= 0)[0])

    # group item by bin
    BinItem = np.zeros((N,M))
    for i in range(N):
        BinItem[i, D[i]] = 1

    # filter bin have w (group by t) < LW
    remove_bin_t = []
    remove_bin_r = []

    for b in range(M):
        item_id = np.where(BinItem[:,b] == 1)[0]

        # group by t
        t_id = np.unique(t[item_id])
        t_item = [item_id[np.where(t[item_id] == idx)[0]] for idx in t_id]
        t_w = [sum(w[item]) for item in t_item]
        t_w_limit = sorted(t_w)[-T[b]:]

        # group by r
        r_id = np.unique(r[item_id])
        r_item = [item_id[np.where(r[item_id] == idx)[0]] for idx in r_id]
        r_w = [sum(w[item]) for item in r_item]
        r_w_limit = sorted(r_w)[-R[b]:]
        
        if sum(t_w_limit) < LW[b]: remove_bin_t.append(b)
        if sum(r_w_limit) < LW[b]: remove_bin_r.append(b)

    # all filtered bin
    remove_bin = remove_bin_dummy0.union(remove_bin_dummy1).union(set(remove_bin_t)).union(set(remove_bin_r))
    
    # remove bin
    D1 = []
    for bin_id in D:
        bin_id1 = [t for t in bin_id if t not in remove_bin]
        D1.append(bin_id1)

    print('Num bin_rmd dummy 0 = %d' % len(remove_bin_dummy0))
    print('Num bin_rmd dummy 1 = %d' % len(remove_bin_dummy1))
    print('Num bin_rmd by t = %d' % len(remove_bin_t))
    print('Num bin_rmd by r = %d' % len(remove_bin_r))
    print('Num bin_rmd all = %d' % len(remove_bin))        
    return D1, remove_bin


# =======================================
# INIT DATA
N,w,p,t,r,D,NT,NR, M,LW,W,P,T,R = read_data(path)
D, remove_item = filter_by_r(N, w, r, D, M, LW, R)
D, remove_bin  = filter_by_bin(N,w,t,r,D, M,LW,W,T,R)

print(list(remove_item))