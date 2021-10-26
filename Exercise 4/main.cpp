#include <functional>
#include <iostream>
#include <list>
#include <mutex>
#include <thread>
#include <vector>
#include <condition_variable>
#include <atomic>
#include <zconf.h>


class Workers {
public:
    std::list<std::function<void()>> tasks;
    std::vector<std::thread> workerThreads;
    std::mutex tasks_mutex;
    std::condition_variable cv;
    int numberOfThreads;

    std::atomic_bool running;

    Workers(int n) {
        numberOfThreads =  n;
    }

    void start() {
        for(int i = 0; i < numberOfThreads; i++) {
            running = true;
            workerThreads.emplace_back([=] {
                while(running) {
                    std::function<void()> task;
                    {
                        std::unique_lock<std::mutex> lock(tasks_mutex);
                        if(!tasks.empty()) {
                            task = *tasks.begin();
                            tasks.pop_front();

                        }else {
                            cv.wait(lock);
                        }
                    }
                    if(task) {
                        task();
                    }
                }
            });
        }
    }

    void post(std::function<void()> task) {
        std::unique_lock<std::mutex> lock(tasks_mutex);
        tasks.emplace_back(task);
        cv.notify_all();
    }

    void stop() {
        running = false;
    }

    void join() {
        for(auto &thread : workerThreads) {
            thread.join();
        }
    }

    void post_timeout(std::function<void()> task, int seconds) {
        post([task, seconds] {
            std::thread tmp([task, seconds] {
                sleep(seconds/1000);
                task();
            });
            tmp.join();
        });
    }
};


int main() {
    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
        std::cout << "Task A from thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.post([] {
        std::cout << "Task B from thread " << std::this_thread::get_id() << std::endl;
    });

    event_loop.post([] {
        std::cout << "Task C from thread " << std::this_thread::get_id() << std::endl;
    });

    event_loop.post([] {
        std::cout << "Task D from thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.post_timeout([] {
        std::cout << "Task E from thread " << std::this_thread::get_id() << std::endl;
    }, 5000);

    worker_threads.post_timeout([] {
        std::cout << "Task F from thread " << std::this_thread::get_id() << std::endl;
    }, 3000);

    worker_threads.join();
    event_loop.join();
}
