import json
from pprint import pprint
import numpy as np
import  matplotlib.pyplot as plt

path = 'data/MinMaxTypeMultiKnapsackInput-3000.json'

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
    print('\nREMOVE INVALID ITEM\n')

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
    print('\nREMOVE INVALID BIN\n')

    # filter bin W <= LW or W <= 0
    remove_bin_dummy0 = set(np.where(W <= LW)[0])
    remove_bin_dummy1 = set(np.where(W <= 0)[0])
    remove_bin_dummy2 = set(np.where(P <= 0)[0])

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
    remove_bin = remove_bin_dummy0.union(remove_bin_dummy1).union(remove_bin_dummy2).union(set(remove_bin_t)).union(set(remove_bin_r))
    
    # remove bin
    D1 = []
    for bin_id in D:
        bin_id1 = [t for t in bin_id if t not in remove_bin]
        D1.append(bin_id1)

    print('Num remove_bin dummy 0 = %d' % len(remove_bin_dummy0))
    print('Num remove_bin dummy 1 = %d' % len(remove_bin_dummy1))
    print('Num remove_bin dummy 2 = %d' % len(remove_bin_dummy2))
    print('Num remove_bin by t = %d' % len(remove_bin_t))
    print('Num remove_bin by r = %d' % len(remove_bin_r))
    print('Num remove_bin all = %d' % len(remove_bin))
    return D1, remove_bin

def filter_by_r1(N,w,r,D, M,LW,R):
    if not np.all(R == 1): return D
    D1 = D.copy()
    print('\nREMOVE INVALID BIN IN EACH R\n')
    # group item by r
    r_id = np.unique(r)
    for idx in r_id:
        r_item = np.where(r==idx)[0]
        r_w = sum(w[r_item])

        # all available bin of r_item
        r_bin = []
        for item in r_item:
            r_bin.extend(D[item])
        r_bin = np.unique(r_bin)

        # filter bin: LW[bin] > r_weight
        r_bin_lw = np.array([LW[j] for j in r_bin])
        remove_bin = r_bin[r_bin_lw > r_w]
        
        # remove bin
        for item in r_item:
            D1[item] = [t for t in D[item] if t not in remove_bin]

        print('R=%3d:\t n_item=%4d, n_remove_bin=%3d/%4d' % (idx, len(r_item), len(remove_bin),len(r_bin)))
    return D1

def add_bin(N,w,p,NT,NR, M,LW,W,P,T,R):
    M = M+1
    LW = np.append(LW,sum(w))
    W =np.append(W,sum(w))
    P =np.append(P,sum(p))
    T =np.append(T,NT)
    R =np.append(R,NR)
    return M,LW,W,P,T,R

def updateConstraint(item, bin):
    w_current[bin] += w[item]
    lw_remain[bin] -= w[item]
    if t_bin_current[bin, t[item]] == 0:
        t_bin_current[bin, t[item]] = 1
        t_count[bin] += 1
    if r_bin_current[bin, r[item]] == 0:
        r_bin_current[bin, r[item]] = 1
        r_count[bin] += 1    

def isValid(item, bin):
    if t_bin_current[bin, t[item]] == 0:
        if t_count[bin] == T[bin]: return False
    if r_bin_current[bin, r[item]] == 0:
        if r_count[bin] == R[bin]: return False
    if w_current[bin] + w[item] > W[bin]: return False
    return True

def greedy_search():
    for r_idx in r_id:
        for t_idx in t_id:
            current_items = t_item[t_idx].intersection(r_item[r_idx])
            if len(current_items) == 0:
                continue
            print(' -------------\t\t %d - %d \t\t------------- ' % (r_idx, t_idx))
            
            candidate_bins = [M-1]
            for item in current_items:
                if len(D[item]) == 0:
                    continue
                bins = candidate_bins.copy()
                while len(bins) > 0:
                    idx = np.argmin(lw_remain[bins])
                    bin = bins.pop(idx)
                    if isValid(item, bin):
                        x[item] = bin
                        print('\t - add item %d to bin %d' % (item, bin))
                        updateConstraint(item, bin)
                        break
                
                if x[item] == M-1:
                    for bin in D[item]:
                        if isValid(item, bin):
                            x[item] = bin
                            print('\t + add item %d to bin %d !!!' % (item, bin))
                            updateConstraint(item, bin)
                            candidate_bins.append(bin)
                            break

def check_result(x, M):
    bin_id = np.unique(sorted(x))

    ok_item = []
    ok_bin = []
    for bin in bin_id:
        if bin == M-1: continue
        bin_item = np.where(x == bin)[0]
        bin_w = sum(w[bin_item])
        t_id = np.unique(t[bin_item])
        r_id = np.unique(r[bin_item])
        if len(r_id) <= R[bin] and len(t_id) <= T[bin]:
            if LW[bin] <= bin_w <= W[bin]:
               ok_bin.append(bin)
               ok_item.extend(bin_item)
    return ok_bin, ok_item, bin_id


# =======================================
# INIT DATA
print('INIT DATA')
N,w,p,t,r,D,NT,NR, M,LW,W,P,T,R = read_data(path)
print('Init data: N = %d, M = %d' % (N, M))

# filter data
D, remove_item = filter_by_r(N, w, r, D, M, LW, R)
D, remove_bin  = filter_by_bin(N,w,t,r,D, M,LW,W,T,R)
D = filter_by_r1(N, w, r, D, M, LW, R)

lenD = np.array([len(t) for t in D])
print('\nMax available item: ', N - len(np.where([len(t)==0 for t in D])[0]))

# add fake bin
M,LW,W,P,T,R = add_bin(N, w, p, NT, NR, M, LW, W, P, T, R)
print('\nAdd fake bin: new M = %d' % M)

# =======================================
# INIT MODEL
print('INIT MODEL')
bin_id = range(M)

t_id = np.unique(sorted(t))
t_item = {j: set(np.where(t==j)[0]) for j in t_id}

r_id = np.unique(sorted(r))
r_item = {j: set(np.where(r==j)[0]) for j in r_id}

# VAR
# item did not packed
x = np.ones(N, np.int16) * (M-1)

# CONSTRAINT
w_current = np.zeros(M)
lw_remain = LW - w_current

t_count = np.zeros(M)
r_count = np.zeros(M)
t_bin_current = np.zeros((M, NT))
r_bin_current = np.zeros((M, NR))


# ======================================
# SEARCH
print('INIT SEARCH')

greedy_search()
# print(list(x))

ok_bin, ok_item, bin_id = check_result(x, M)
print('Feasible bin: %d / %d; item: %d / %d ' % (len(ok_bin),len(bin_id), len(ok_item), N))