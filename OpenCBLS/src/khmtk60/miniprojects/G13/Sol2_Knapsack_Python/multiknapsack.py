import json
import numpy as np
import time
from tqdm import tqdm


class MultiKnapsack:
    path = 'data/MinMaxTypeMultiKnapsackInput-3000.json'

    def __init__(self):
        with open(self.path) as f:
            data = json.load(f)

        self.w = [item['w'] for item in data['items']]
        self.p = [item['p'] for item in data['items']]
        self.t = [item['t'] for item in data['items']]
        self.r = [item['r'] for item in data['items']]
        self.D = [item['binIndices'] for item in data['items']]
        self.n = len(self.w)

        self.LW = [pack['minLoad'] for pack in data['bins']]
        self.W = [pack['capacity'] for pack in data['bins']]
        self.P = [pack['p'] for pack in data['bins']]
        self.T = [pack['t'] for pack in data['bins']]
        self.R = [pack['r'] for pack in data['bins']]
        self.m = len(self.W)

        self.nt = int(max(self.t)+1)
        self.nr = int(max(self.r)+1)

        # ADD NEW BIN
        self.D = [t+[self.m] for t in self.D]
        self.m += 1
        self.LW.append(0)
        self.W.append(sum(self.w))
        self.P.append(sum(self.p))
        self.T.append(self.nt)
        self.R.append(self.nr)

        # remove LW
        # LW = [0 for t in LW]

        self.w = np.array(self.w)
        self.p = np.array(self.p)
        self.t = np.array(self.t) + 1
        self.r = np.array(self.r) + 1 # t and r must be not equal to 0, for using np multiply
        self.LW = np.array(self.LW)
        self.W = np.array(self.W)
        self.P = np.array(self.P)
        self.T = np.array(self.T)
        self.R = np.array(self.R)

        self.ignored_bin = []  # self.ignore_invalid_bin()
        # local search variable
        # can chuan hoa lai id cua t va r # tu 0 -> n_types, 0 -> n->bin
        # de su dung trong bin_t, bin_r
        self.x = np.zeros([self.n, self.m])

        ids_t = list(np.unique(self.t))
        n_types = np.sum(ids_t)
        self.bin_t = np.zeros([n_types, self.m])
        self.t = [ids_t.index(ti) for ti in self.t]

        ids_r = list(np.unique(self.r))
        n_classes = np.sum(ids_r)
        self.bin_r = np.zeros([n_classes, self.m])
        self.r = [ids_r.index(ri) for ri in self.r]

        #
        self.current_n_items_in_bin = np.zeros(self.m)

        # MODEL
        self.W_violations = np.array([0] * self.m)
        self.LW_violations = np.array([0] * self.m)
        self.P_violations = np.array([0] * self.m)
        self.T_violations = np.array([0] * self.m)
        self.R_violations = np.array([0] * self.m)

        # upper bounds
        self.W_upper_bound, self.LW_upper_bound, self.P_upper_bound, self.T_upper_bound, self.R_upper_bound = 1, 1, \
                                                                                                              1, 1, 1

        #
        self.n_satisfied_items = 0
        self.current_violations = 0

        # normalize w, p, W, LW, P
        max_w_W_LW = max(list(self.w) + list(self.W) + list(self.LW))
        max_p_P = max(list(self.p) + list(self.P))

        # TODO
        # # need to config coef
        # self.w = self.normalize_violations(self.w, max_w_W_LW)
        # self.W = self.normalize_violations(self.W, max_w_W_LW)
        # self.LW = self.normalize_violations(self.LW, max_w_W_LW)
        # self.p = self.normalize_violations(self.p, max_p_P)
        # self.P = self.normalize_violations(self.P, max_p_P)


    def set_upper_bounds(self, W_ub, LW_ub, P_ub, T_ub, R_ub):
        self.W_upper_bound, self.LW_upper_bound, self.P_upper_bound, self.T_upper_bound, self.R_upper_bound \
            = W_ub, LW_ub, P_ub, T_ub, R_ub

    def normalize_violations(self, vector, max_value, coef=1):
        return vector / max_value * coef

    def ignore_invalid_bin(self):
        print("Ignoring bins")
        W = np.array(self.W)
        LW = np.array(self.LW)
        P = np.array(self.P)

        ignored_bin_1 = set(np.where(W <= 0)[0])
        ignored_bin_2 = set(np.where(W <= LW)[0])
        ignored_bin_3 = set(np.where(P <= 0)[0])
        ignored_bin = list(ignored_bin_1.union(ignored_bin_2).union(ignored_bin_3))

        for i in tqdm(range(len(self.D))):
            for bin in (ignored_bin):
                if bin in self.D[i]:
                    self.D[i].remove(bin)

        return ignored_bin


    def init_solution(self):
        '''
        Random take each item into random bin.
        :return:
        '''
        self.x = np.zeros([self.n, self.m])

        # for i in range(self.n):
        #     di = self.D[i]
        #     b = np.random.choice(di)
        #     self.x[i][b] = 1
        #     self.bin_t[self.t[i]][b] += 1
        #     self.bin_r[self.r[i]][b] += 1

        with open('solution/solution_gr.json') as f:
            data = json.load(f)
            bins = list(data["binIndex"])
            for i, b in enumerate(bins):
                # print(i, b)
                self.x[i][b] = 1
                self.bin_t[self.t[i]][b] += 1
                self.bin_r[self.r[i]][b] += 1

        # with open('solution/g15_result_3k.txt', 'r') as f:
        #     line = f.readline()
        #     line.replace("-1", "1846")
        #     line = line.split(' ')[:-1]
        #     bins = [int(b) for b in line]
        #
        #     for i, b in enumerate(bins):
        #         # print(i, b)
        #         self.x[i][b] = 1
        #         self.bin_t[self.t[i]][b] += 1
        #         self.bin_r[self.r[i]][b] += 1

        self.current_n_items_in_bin = np.sum(self.x, axis=0)

    def get_violations_with(self, violations, upper_bound=1):
        '''
        Convert negative violations in to real violations
        :param violations: current violations (which can have negative values)
        :return:
        '''
        if type(violations) == np.ndarray:
            value = np.array(violations.copy())
            value[value < 0] = 0
            # value[value > 0] = upper_bound  # make all constraint are equal
            return value
        else:
            if violations < 0:
                return 0
            # elif violations > 0:
            #     return upper_bound
            return violations

    def update_W_violations(self):
        """
        Update weight 1 violations

            W_violations will have negative values (easier in calculating getAssignDelta).
            If we need to convert into real violations, we can use get_violations_with(self.W_violations)
        :return:
        """

        result = self.w.reshape([self.n, 1]).transpose() @ self.x - self.W  # tong cua item trong bin tru W cua bin
        self.W_violations = np.reshape(result, [-1])

    def update_LW_violations(self):
        '''
        Update lower weight 1 violations

        Solution 1:
            There are 2 cases:
            1. No item -> No violations
            2. 1+ item: LW_violations_bin = LW_bin - total_weight_of_items_in_bin

        Solution 2: Calculate all violations
        :return:
        '''
        # solution 1
        result = self.LW - self.w.reshape([self.n, 1]).transpose() @ self.x
        n_items_in_bins = np.sum(self.x, axis=0)
        n_items_in_bins[n_items_in_bins > 0] = 1
        self.LW_violations = np.reshape(result, [-1]) * np.reshape(n_items_in_bins, [-1])
        #np.reshape(np.multiply(result, n_items_in_bins), [-1])

        # solution 2
        # result = self.LW - self.w.reshape([self.n, 1]).transpose() @ self.x
        # self.LW_violations = np.reshape(result, [-1])

    def update_P_violations(self):
        result = self.p.reshape([self.n, 1]).transpose() @ self.x - self.P
        self.P_violations = np.reshape(result, [-1])

    # t and r must be not equal to 0, for using np multiply
    def update_T_violations(self):
        result = np.multiply(np.transpose([np.array(self.t) + 1] * self.m), self.x)

        T_current = np.zeros(self.m)
        for i in range(result.shape[1]):
            T_current[i] = np.count_nonzero(np.unique(result[:, i]))

        result = T_current - self.T
        self.T_violations = result

    def update_R_violations(self):
        result = np.multiply(np.transpose([np.array(self.r) + 1] * self.m), self.x)

        R_current = np.zeros(self.m)
        for i in range(result.shape[1]):
            R_current[i] = np.count_nonzero(np.unique(result[:, i]))

        result = R_current - self.R
        self.R_violations = result

    def update_violations(self):
        self.update_W_violations()
        self.update_LW_violations()
        self.update_P_violations()
        self.update_T_violations()
        self.update_R_violations()
        self.current_violations = np.sum(self.get_violations_with(self.W_violations, self.W_upper_bound)
                                         + self.get_violations_with(self.LW_violations, self.LW_upper_bound)
                                         + self.get_violations_with(self.P_violations, self.P_upper_bound)
                                         + self.get_violations_with(self.T_violations, self.T_upper_bound)
                                         + self.get_violations_with(self.R_violations, self.R_upper_bound))

    def get_violations(self):
        return self.current_violations

    def get_all_bin_violations(self):
        return self.get_violations_with(self.W_violations, self.W_upper_bound)\
               + self.get_violations_with(self.LW_violations, self.LW_upper_bound)\
               + self.get_violations_with(self.P_violations, self.P_upper_bound)\
               + self.get_violations_with(self.T_violations, self.T_upper_bound)\
               + self.get_violations_with(self.R_violations, self.R_upper_bound)

    def print_violations(self):
        print("W violations:", self.W_violations, "\t total: ", np.sum(self.get_violations_with(self.W_violations, self.W_upper_bound)))
        print("LW violations:", self.LW_violations, "\t total: ", np.sum(self.get_violations_with(self.LW_violations, self.LW_upper_bound)))
        print("P violations:", self.P_violations, "\t total: ", np.sum(self.get_violations_with(self.P_violations, self.P_upper_bound)))
        print("T violations:", self.T_violations, "\t total: ", np.sum(self.get_violations_with(self.T_violations, self.T_upper_bound)))
        print("R violations:", self.R_violations, "\t total: ", np.sum(self.get_violations_with(self.R_violations, self.R_upper_bound)))
        print("Total violations:", self.get_violations())

    def set_value_propagate(self, i, j, k):
        self.x[i, j] = k

        change_value = (k - 0.5) * 2  # 0 -> -1;  1 -> 1
        self.bin_t[self.t[i], j] += change_value
        self.bin_r[self.r[i], j] += change_value
        self.update_violations()

    def get_assign_delta(self, i, j, k):
        # ERROR: when move item from one bin to last bin
        # NOTE: current version **just can be used for swap** or **one variable assignment**
        # gia su gan x[i][j] = k (k = 0 hoac k = 1)
        # anh huong den cac constraint nhu nao?
        # anh huong W, LW, P, T, R cua bin j
        # phai tinh ca 2 gia tri truoc va sau khi thay doi
        # truoc khi thay doi thi da co san o W_violations, LW_violations, ...
        # old_value = self.x[i, j]
        # new value self.x[i, j] = k

        # # anh huong nhu nao den current violation?
        # total_delta_W_violations, total_delta_LW_violations, total_delta_P_violations, \
        # total_delta_T_violations, total_delta_R_violations = 0, 0, 0, 0, 0
        #
        # for (i, j, k) in zip(list_i, list_j, list_k):
        # W
        # new_W_bin = self.w.reshape([self.n, 1]).transpose() @ self.x[:, j] # ton thoi gian?
        # there is one disadvantage here when using upper bound: when bin are violated, more violations are not added.
        delta_w = self.w[i] * (k - self.x[i, j])  # 1 or -1 when swap
        new_W_violations = self.W_violations[j] + delta_w
        delta_W_violations = self.get_violations_with(new_W_violations, self.W_upper_bound) -\
                             self.get_violations_with(self.W_violations[j], self.W_upper_bound)

        # LW: xet them truong hop neu bin chua chua vat nao thi delta_LW_violations = LW - self.w[i] * k
        # if np.sum(self.x[:, j]) == 0:
        #     delta_LW_violations = self.get_violations_with(self.LW[j] - self.w[i] * k)
        # else:
        delta_lw = self.w[i] * (k - self.x[i, j])
        # TODO
        # check case 1->0, 0->1
        new_LW_violations = self.LW_violations[j] - delta_lw
        if self.current_n_items_in_bin[j] == 0: #np.sum(self.x[:, j]) == 0:  # neu bin khong chua item nao
            new_LW_violations = self.LW[j] - self.w[i]

        delta_LW_violations = self.get_violations_with(new_LW_violations, self.LW_upper_bound)\
                              - self.get_violations_with(self.LW_violations[j], self.LW_upper_bound)


        # P
        delta_p = self.p[i] * (k - self.x[i, j])
        new_P_violations = self.P_violations[j] + delta_p
        delta_P_violations = self.get_violations_with(new_P_violations, self.P_upper_bound) \
                             - self.get_violations_with(self.P_violations[j], self.P_upper_bound)

        # T
        # T_bin = np.multiply(np.transpose(self.t), self.x[:, j])
        # T_bin = list(T_bin)
        #
        # n_uniques_old = np.count_nonzero(np.unique(T_bin))
        # n_uniques = 0
        # if self.x[i][j] == 0 and k == 1:
        #     T_bin.append(self.t[i])
        #     n_uniques = np.count_nonzero(np.unique(T_bin))
        # elif self.x[i][j] == 1 and k == 0:
        #     T_bin.remove(self.t[i])
        #     n_uniques = np.count_nonzero(np.unique(T_bin))
        delta_t = 0
        if self.bin_t[self.t[i], j] == 0 and k == 1:
            delta_t = 1
        elif self.bin_t[self.t[i], j] == 1 and k == 0:
            delta_t = -1

        # delta_t = n_uniques - n_uniques_old
        new_T_violations = self.T_violations[j] + delta_t
        delta_T_violations = self.get_violations_with(new_T_violations, self.T_upper_bound) \
                             - self.get_violations_with(self.T_violations[j], self.T_upper_bound)

        # R
        # R_bin = np.multiply(np.transpose(self.r), self.x[:, j])
        # R_bin = list(R_bin)
        #
        # n_uniques_old = np.count_nonzero(np.unique(R_bin))
        # n_uniques = 0
        # if self.x[i][j] == 0 and k == 1:
        #     R_bin.append(self.r[i])
        #     n_uniques = np.count_nonzero(np.unique(R_bin))
        # elif self.x[i][j] == 1 and k == 0:
        #     R_bin.remove(self.r[i])
        #     n_uniques = np.count_nonzero(np.unique(R_bin))
        #
        # delta_r = n_uniques - n_uniques_old
        delta_r = 0
        if self.bin_r[self.r[i], j] == 0 and k == 1:
            delta_r = 1
        elif self.bin_r[self.r[i], j] == 1 and k == 0:
            delta_r = -1

        new_R_violations = self.R_violations[j] + delta_r
        delta_R_violations = self.get_violations_with(new_R_violations, self.R_upper_bound) \
                             - self.get_violations_with(self.R_violations[j], self.R_upper_bound)

        return np.array([delta_W_violations, delta_LW_violations, delta_P_violations, delta_T_violations, delta_R_violations]), \
               (new_W_violations, new_LW_violations, new_P_violations, new_T_violations, new_R_violations)

    def get_swap_delta(self, i, j1, j2):
        # TODO
        # phai tinh dong thoi -> sua lai ham get assign delta de chap nhan list
        result = [0, 0, 0, 0, 0]
        delta_1, new_violations_1 = self.get_assign_delta(i, j1, self.x[i, j2])
        delta_2, new_violations_2 = self.get_assign_delta(i, j2, self.x[i, j1])

        return delta_1, delta_2, new_violations_1, new_violations_2

    def set_swap_propagate(self, i, j1, j2):
        # self.update_violations()

        self.update_violations_with_swap(i, j1, j2)

        # set bin_t, bin_r
        self.bin_t[self.t[i], j1] += (self.x[i, j2] - 0.5) * 2
        self.bin_t[self.t[i], j2] += (self.x[i, j1] - 0.5) * 2

        self.bin_r[self.r[i], j1] += (self.x[i, j2] - 0.5) * 2
        self.bin_r[self.r[i], j2] += (self.x[i, j1] - 0.5) * 2

        self.current_n_items_in_bin[j1] += (self.x[i, j2] - 0.5) * 2
        self.current_n_items_in_bin[j2] += (self.x[i, j1] - 0.5) * 2

        temp = self.x[i, j1]
        self.x[i, j1] = self.x[i, j2]
        self.x[i, j2] = temp





    def update_violations_with_swap(self, i, j1, j2):
        delta_1, delta_2, new_violations_1, new_violations_2 = self.get_swap_delta(i, j1, j2)
        # ERROR
        # sao delta > 0 lai duoc chon, dang ra phai bang 0
        # violation nay co the am
        self.W_violations[j1], self.LW_violations[j1], self.P_violations[j1], \
        self.T_violations[j1], self.R_violations[j1] = new_violations_1

        self.W_violations[j2], self.LW_violations[j2], self.P_violations[j2], \
        self.T_violations[j2], self.R_violations[j2] = new_violations_2

        # khong duoc dung delta?
        self.current_violations += np.sum(delta_1 + delta_2)

    def get_numbers_of_satisfied_items_and_violations(self):
        violations = self.get_all_bin_violations()[:-1]
        items_in_bins = self.current_n_items_in_bin[:-1]  # np.sum(self.x, axis=0)[:-1]  #

        n_satisfied_items = np.sum(items_in_bins[violations == 0])
        return n_satisfied_items, np.sum(violations)

    def objective_function(self):
        # TODO
        # numbers of satisfied item and violations
        # current violations mostly focus on LW, we need to decrease the coefficient of LW
        return self.n_satisfied_items * 5 + self.get_violations()

    def search(self):
        """
        Search Stragety

        Loop:
            Step 1: Try to take all items from last bin to another bin which will not violate (because current_violations = 0) (k times)
            Step 2: Distribute items from last bin to 0-bin (bin with 0 item)
            Step 3: Local Search for all item, k times for each item
            Step 4: Move all items from violating bin to last bin


        :return:
        """
        print("INIT VIOLATIONS:", self.current_violations)

        while True:
            # TODO
            # viec khong co objective funtion lien ket giua item va violations la 1 han che, lam giam di so item hien thoa man (tru khi nem vao bin cuoi)
            # muc tieu phai la vua tang item, vua giam violations
            for i in range(2):
                self.solve_last_bin()

            self.distribute_items_from_last_bin()
            self.random_search(n_steps=10000)
            self.gather_items_from_violating_bin_to_last_bin()

            # break



    def solve_last_bin(self):
        # bin cuoi
        bin_index = self.m - 1

        # lay nhung item trong bin day ra
        item_indexes = np.where(self.x[:, bin_index] == 1)[0]
        print("-------------------------------")
        print("Solving Last BIN")
        print("-------------------------------")

        # changed = True
        # while(changed):
        #     changed = False
        np.random.shuffle(item_indexes)
        for i, item in enumerate(item_indexes):
            start_time = time.time()
            di = self.D[item]
            arr_delta_violations = []

            for d in di:
                delta_1, delta_2, _, _ = self.get_swap_delta(item, bin_index, d)
                arr_delta_violations.append(np.sum(delta_1 + delta_2))
            arr_delta_violations = np.array(arr_delta_violations)
            index_min = np.argmin(arr_delta_violations)
            indexes_min = np.where(arr_delta_violations == arr_delta_violations[index_min])[0]
            index_min = np.random.choice(indexes_min)
            next_bin = di[index_min]

            self.set_swap_propagate(item, bin_index, next_bin)

            n_items, violations = self.get_numbers_of_satisfied_items_and_violations()
            if n_items > self.n_satisfied_items:
                self.get_solution_file()
                self.n_satisfied_items = n_items

            print("Step, ", i, "\tViolations:", np.sum(violations), "\t Items: ", n_items, "\t------>\t Take item ", item,
                  "\tfrom bin ", bin_index, "\tto bin", next_bin, "\t in", time.time() - start_time,
                  "seconds")

    def distribute_items_from_last_bin(self):
        # sau khi xu ly xong last bin, can nem tat ca item hien dang o bin cuoi sang nhung bin khac dang vi pham rang buoc
        print("-------------------------------")
        print("Taking items from last bin to another bin")
        print("-------------------------------")

        selected_bins = []
        last_bin = self.m - 1

        # bin_violations = self.get_all_bin_violations()
        # for i, vio in enumerate(self.get_all_bin_violations()[::-1]):
        #     if np.sum(self.x[:, last_bin - i]) == 0:
        #         selected_bins.append(last_bin - i)
        for i, vio in enumerate(self.get_all_bin_violations()):
            if (vio > 0) or (self.current_n_items_in_bin[i] == 0):
                selected_bins.append(i)

        last_bin_items = np.where(self.x[:, last_bin] == 1)[0]

        available_bins = set(selected_bins).difference(self.ignored_bin)

        count = 0
        for i in tqdm(last_bin_items):
            # nam trong selected_bins intersection self.D[i] - self.ignored_bin
            available_bins = list(set(available_bins).intersection(set(self.D[i])))

            if len(available_bins) == 0:
                selected_bin = self.m - 1
            else:
                selected_bin = np.random.choice(available_bins)
                count += 1
            if selected_bin != last_bin:
                start_time = time.time()
                self.set_swap_propagate(i, last_bin, selected_bin)
                n_items, violations = self.get_numbers_of_satisfied_items_and_violations()
                print("Step, ", count, "\tViolations:", np.sum(violations), "\t Items: ", n_items, "\t------>\t Take item ",
                      i,
                      "\tfrom bin ", last_bin, "\tto bin", selected_bin, "\t in", time.time() - start_time,
                      "seconds")

        self.update_violations()
        print("-------------------------------")
        print("---- MOVED", count, "ITEMS")
        print("After move from last bin to violated bin, violations =", self.current_violations)
        print("-------------------------------")
        # sau khi phan phoi nhu nay, cac bin rang buoc moi bi rang buoc nhieu -> co the nem vao bin da thoa man
        # lam giam so items ma van giam duoc so luong rang buoc
        # mat khac, no co the nem vao bin cuoi de giam het rang buoc

    def random_search(self, n_steps=1000):
        print("-------------------------------")
        print("SEARCHING .............")
        print("-------------------------------")
        it = 0
        while (it < n_steps):
            start_time = time.time()
            random_i = np.random.randint(self.n)
            current_bin = np.where(self.x[random_i, :] == 1)[0][0]
            di = self.D[random_i].copy()
            # di = np.random.choice(di, min(50, len(di))) # random 50 bins
            # di[-1] = current_j

            start_time_get_swap = time.time()
            arr_delta_violations = []

            # choose next bin
            next_bins = []

            for d in di:
                delta_1, delta_2, new_violations_1, new_violations_2 = self.get_swap_delta(random_i, current_bin, d)
                # TODO
                # bin day nem vao bin khong day (1+)
                # error: lay 1 item nem vao bin cuoi, vio tang
                if np.sum(delta_1 + delta_2) <= 0 and np.sum(delta_2) <= 0 and \
                        (np.sum(self.get_violations_with(np.array(new_violations_2))) <= 0 or d == current_bin):
                # if np.sum(delta_1 + delta_2) <= 0 and np.sum(delta_2) <= 0:
                    next_bins.append(d)
                    # next_bin = d
                    # break
                    # arr_delta_violations.append(np.sum(delta_1 + delta_2))

            if (self.m - 1) in next_bins:
                next_bins.remove(self.m - 1)
            if len(next_bins) == 0:
                next_bin = self.m - 1
            else:
                next_bin = np.random.choice(next_bins)

            end_time_get_swap = time.time()

            start_time_propagate = time.time()
            # next_bins.append(self.m - 1)
            # next_bin = np.random.choice(next_bins)
            if current_bin != next_bin:
                self.set_swap_propagate(random_i, current_bin, next_bin)
            end_time_propagate = time.time()

            n_items, violations = self.get_numbers_of_satisfied_items_and_violations()
            # if n_items > self.n_satisfied_items:
            #     self.get_solution_file()
            #     self.n_satisfied_items = n_items
            # violations = self.get_violations()
            print("Step, ", it,
                  # "\tViolations:", violations, "\t Items: ",
                  "\tViolations:", np.sum(violations), "\t Items: ",
                  n_items, "\t------>\t Take item ", random_i,
                  "\tfrom bin ", current_bin, "\tto bin", next_bin,
                  "\tget swap time:", "{:2.2}".format(end_time_get_swap - start_time_get_swap),
                  "\t propagate time:", "{:2.2}".format(end_time_propagate - start_time_propagate),
                  "\t in", "{:2.2}".format(time.time() - start_time), "s")

            it += 1

    def gather_items_from_violating_bin_to_last_bin(self):
        self.update_violations()
        print("-------------------------------")
        print("GATHERING ITEMS FROM VIOLATING BIN TO LAST BIN")
        print("CURRENT VIOLATIONS =", self.current_violations)
        print("-------------------------------")
        bin_violations = self.get_all_bin_violations()
        bin_indexes = np.where(bin_violations > 0)[0]


        i = 0
        for bin_index in bin_indexes:
            item_indexes = np.where(self.x[:, bin_index] == 1)[0]
            np.random.shuffle(item_indexes)
            for item in item_indexes:
                start_time = time.time()
                last_bin = self.m - 1

                self.set_swap_propagate(item, bin_index, last_bin)
                n_items, violations = self.get_numbers_of_satisfied_items_and_violations()

                i += 1
                print("Step, ", i, "\tViolations:", np.sum(violations), "\t Items: ", n_items, "\t------>\t Take item ",
                      item,
                      "\tfrom bin ", bin_index, "\tto bin", last_bin, "\t in", time.time() - start_time,
                      "seconds")


        self.update_violations()
        print("-------------------------------")
        print("---- MOVED", i, "ITEMS")
        print("After move from violated bin to last bin, violations =", self.current_violations)
        print("-------------------------------")



    def search_with_most_violated_bins(self, bin_index):
        item_indexes = np.where(self.x[:, bin_index] == 1)[0]
        print("-------------------------------")
        print("Solving One Of Most Violated BIN")
        print("-------------------------------")

        np.random.shuffle(item_indexes)
        for i, item in enumerate(item_indexes):
            start_time = time.time()
            di = self.D[item]
            arr_delta_violations = []

            for d in di:
                delta_1, delta_2, _, _ = self.get_swap_delta(item, bin_index, d)
                arr_delta_violations.append(np.sum(delta_1 + delta_2))
            arr_delta_violations = np.array(arr_delta_violations)
            index_min = np.argmin(arr_delta_violations)
            indexes_min = np.where(arr_delta_violations == arr_delta_violations[index_min])[0]
            index_min = np.random.choice(indexes_min)
            next_bin = di[index_min]


            self.set_swap_propagate(item, bin_index, next_bin)

            n_items, violations = self.get_numbers_of_satisfied_items_and_violations()
            if n_items > self.n_satisfied_items:
                self.get_solution_file()
                self.n_satisfied_items = n_items

            print("Step, ", i, "\tViolations:", np.sum(violations), "\t Items: ", n_items, "\t------>\t Take item ",
                  item,
                  "\tfrom bin ", bin_index, "\tto bin", next_bin, "\t in", time.time() - start_time,
                  "seconds")

    def get_solution_file(self, path='solution/solution.json'):

        with open(path, 'w') as f:
            bin_indexes = []
            for i in range(self.n):
                bin_index = int(np.where(self.x[i, :])[0][0])
                bin_indexes.append(bin_index)
            bin_indexes = list(bin_indexes)
            solution = {"binIndex": bin_indexes}
            json.dump(solution, f)

    def test(self):
        self.gather_items_from_violating_bin_to_last_bin()
        self.get_solution_file(path="solution/test_solution.json")

solver = MultiKnapsack()
solver.init_solution()
solver.set_upper_bounds(1, 1, 1, 1, 1)
solver.update_violations()
solver.search()
# solver.test()