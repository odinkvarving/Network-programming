#include <iostream>
#include <thread>
#include <vector>
#include <algorithm>
#include <mutex>
#include <cmath>

using namespace std;


mutex mtx;
vector<int> primes;

int findPrimes(int low, int high) {
    int i, j, flag;

    mtx.lock();
    for (i = low; i <= high; i++) {
        if (i == 1 || i == 0) {
            continue;
        }
        flag = 1;

        for (j = 2; j <= sqrt(i); j++) {
            if (i % j == 0) {
                flag = 0;
                mtx.unlock();
                break;
            }
        }

        bool found = (std::find(primes.begin(), primes.end(), i) != primes.end());
        if (flag == 1 && !found) {
            primes.push_back(i);
            printf("%d ", i);
            mtx.unlock();
        }
    }
    return 0;
}


int main() {

    int low = 0, high = 0, numberOfThreads;
    vector<int> numberList;
    vector<thread> threads;

    cout << "Enter two numbers (intervals): ";
    cin >> low >> high;
    cout << "Enter number of threads: ";
    cin >> numberOfThreads;

    for (int i = low; i <= high ; i++) {
        numberList.push_back(i);

    }

    int minimumLoad = ((high + 1) - low) / numberOfThreads;
    int rest = (high - low) % numberOfThreads;
    double additionalLoad = rest / numberOfThreads;
    int extraLoad = (int) additionalLoad + 2;

    vector<int> numbersPerThread;
    auto iterator = numberList.begin();

    for(int n = 0; n < numberOfThreads; n++) {

        if(rest != 0) {
            for (int i = 0; i < minimumLoad + extraLoad; i++) {
                numbersPerThread.push_back(*iterator);
                iterator++;
            }
            rest--;
        }else {
            for(int j = 0; j < minimumLoad; j++) {
                numbersPerThread.push_back(*iterator);
                iterator++;
            }
        }
        threads.emplace_back(findPrimes, numbersPerThread.front(), numbersPerThread.back());
        numbersPerThread.clear();
    }

    for (thread &t : threads) {
        t.join();
    }

    sort(primes.begin(), primes.end());
    cout << "\nPrimes sorted in ascending order: " << endl;
    for (int prime : primes) {
        printf("%d ", prime);
    }
}


