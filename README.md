# Introduction

本项目实践了许多想要解决 critical section 问题的解法，虽然有些早期的解法是存在错误的，但是在代码中实地 run 一下更有利于我们学习这些方法的背后的思想以及它们的局限。

This is a set of solutions which try to solve critical section problem. Some of them are old methods which are not correct but it is meaningful for us to practice and see how they make mistakes.

# Busy Waiting

Busy waiting 是一种比较直观的解决思路。其代码形式如下：

```
volatile lock = 0;
process i:
    while (lock == 1);
    lock = 1;
    execute CS;
    lock = 0;
```

在 **src/BusyWaiting**, 有一个 demo 可以实际跑一下 BusyWaiting

```
void run(int n) throws InterruptedException {
    BusyInstance[] busyInstances = new BusyInstance[n];
    for (int i = 0; i < n; i++) {
        busyInstances[i] = new BusyInstance(i);
        busyInstances[i].start();
    }
    for (int i = 0; i < n; i++) {
        busyInstances[i].join();
    }
    System.out.println("Finally, a is " + a);
}

@Override
public void run(){
    for (int i = 0; i < 5; i++) {
        while (lock == 1);
        lock = 1;
        a = a + 1;
        System.out.println("Instance"+id+": a = "+a);
        lock = 0;
    }
}
```

这个 demo 建立了 n = 10 个线程，每个线程对 a 执行5次加法，如果这个方法保证 CS 是线程安全的话，那么最后 a 的结果应该是50.

## View Output

以下是运行结果，从结果来看 a = 44 明显说明我们在执行多线程加法的时候，出现了 dirty read 或者 lost update 的情况。Busy Waiting 仍然会出现 race condition.


```
Instance0: a = 1
Instance0: a = 2
Instance0: a = 3
Instance0: a = 4
Instance0: a = 5
Instance1: a = 2
Instance3: a = 6
Instance3: a = 8
Instance3: a = 9
Instance3: a = 10
Instance3: a = 12
Instance2: a = 2
Instance5: a = 13
Instance5: a = 15
Instance4: a = 12
Instance4: a = 17
Instance1: a = 7
Instance1: a = 19
Instance4: a = 18
Instance6: a = 16
Instance6: a = 22
Instance9: a = 23
Instance5: a = 16
Instance2: a = 14
Instance2: a = 26
Instance9: a = 25
Instance9: a = 28
Instance9: a = 29
Instance5: a = 25
Instance6: a = 24
Instance8: a = 22
Instance4: a = 21
Instance1: a = 20
Instance7: a = 19
Instance1: a = 35
Instance4: a = 34
Instance8: a = 33
Instance6: a = 32
Instance5: a = 31
Instance9: a = 30
Instance2: a = 27
Instance6: a = 38
Instance8: a = 37
Instance7: a = 36
Instance7: a = 41
Instance8: a = 40
Instance2: a = 39
Instance8: a = 43
Instance7: a = 42
Instance7: a = 44
Finally, a is 44
```

# TestAndSet

自旋锁（Test and Set）可以避免 Busy Waiting 中出现的 race condition。基于 BusyWaiting 的代码，可以略作修改，改成一下形式

```java
volatile lock = 0;
process i:
    while (test_and_set(lock) == 1);
    execute CS;
    lock = 0;

test_and_set(lock) {
    int temp = lock;
    lock = 1;
    return temp;
}
```


## TestAndSetDeadlock

自旋锁会导致死锁，因为当 lock = 1 时，线程仍然在持续地运行着而不是沉睡，所以称之为自旋。在 src/TestAndSetDeadlock 中 demo 给出了一个利用 synchronized 实现自旋锁的例子，在运行过程中，会出现死锁现象。

```
public void run(){
    for (int i = 0; i < 5; i++) {
        while (testAndSet() == 1);
        a++;
        System.out.println("Instance"+id+": a = "+a);
        lock = 0;
    }
}

synchronized private int testAndSet() {
    int temp = lock;
    lock = 1;
    return temp;
}
```

## TestAndSetNoDeadlock

为了避免死锁，可以使用 Java 的类库中给出的 AtomicBoolean、AtomicInteger.

[src/TestAndSetNoDeadlock.java](src/TestAndSetNoDeadlock.java) 是一个具体实现的 demo.

```
AtomicBoolean lock = new AtomicBoolean(false);

@Override
public void run(){
    for (int i = 0; i < 5; i++) {
        while (lock.getAndSet(true));
        a++;
        System.out.println("Instance"+id+": a = "+a);
        lock.set(false);
    }
}
```

# semaphore

旗标是解决 CS 问题的另一种方法。S is an integer，P(S) 和 V(S) 是原子级操作。

P: 如果 S >= 1 则运行当前程序，S = S - 1，否则 block 线程并将任务入队；

S: 如果队列中有任务则取出任务，否则 S = S + 1;

semaphore 的性质是 P 可能 block a process 但是 V 不会。

inital value of S 是1，如果有多个 resource 则 intial value = N；

```
P(S): if S ≥ 1 then S := S - 1
	  else <block and enqueue the process>;
V(S): if <some process is blocked on the queue> then
			<unblock a process>
	  else S := S + 1;
	  
Shared var mutex: semaphore = 1;
Process i
begin
.
P(mutex);
execute CS;
V(mutex);
.
End;
```







