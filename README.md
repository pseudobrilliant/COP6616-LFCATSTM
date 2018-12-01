# Lock Free Contention Adaptive Trees

The prevalence of larger and larger datasets necessitates modifications to the means
by which these data are stored and accessed.  To efficiently increase the throughput
of database operations under heavy load, the problem of data contention must be ad-
dressed.  Windblad et al, did so by developing a lock-free binary tree that dynamically
adjusts the distribution of the data within the search tree.  In this paper, we seek to
implement the basic structure and functionality of their lock-free contention adapting
search tree (LFCAT). Our results will show the throughput of our implementation measured 
under varying concurrency and operational loads.

## Getting Started

For ease of use we have included a precompiled version of the code available under 
the dist folder of this repository. To get accurate results you must run the STM implementation with Deuce STM as a 
javaagent.

To run the jar simply use 'java -javaagent <DEUCE AGENT JAR> -jar COP6616-LFCATSTM.jar'

To build the code please use the intellij IDE (or similar) and configure the following:
* Right click the project folder and select 'Open Module Settings'
* In the project tab make sure to use JDK 7 and set your output directory
* Int the libraries section grab the prerequisite libraries from maven
* Back in the project directory right click the src directory and 'Mark Directory As' -> Sources
* Right click the output directory and 'Mark Directory As' -> Output
* In the top right add a configuration that uses the Main class from the COP6616 directory.

### Prerequisites

Must use JDK 7 to use the COP6616-LFCATSTM.jar in the distribution.

To build or run you must have the following libraries:
* Deuce STM 1.30
* xChart 3.5.2

Deuce STM can be found here https://github.com/DeuceSTM/DeuceSTM

### Results Over STM Implementation
The figure below shows the performance of a comparable binary search tree implemented using the Deuce Software Transactional 
Memory Library (Deuce-STM). Our Deuce-STM implementation is essentially a binary search tree with insert, remove, contains, 
and range query support. It does not provide any of the contention adaptation methods described above, but rather lets the 
operations resolve as STM transactions. The algorithm performs best using a single thread and degrades as more threads are 
used. The library does not efficiently perform synchronization between threads. As the number of threads increases, more 
operations overlap and must be restarted. Since the operation of updating and replacing an AVL tree in the leaf nodes is 
expensive, having to restart, re-copy, update, and re-balance the new tree any time a conflict is encountered tanks the 
performance. Increasing the transaction size would further reduce the performance since more conflicts would occur which 
would require more operations to restart. Overall, STM is not a good approach for the LFCAT algorithm. The LFCAT is highly 
optimized for the task at hand and the STM library cannot automatically optimize the algorithm better than human efforts. 
Additionally, the contention adaptation presented in this paper is not well suited for STM libraries because split and join 
are specifically designed to avoid conflicts with overlapping method calls from other threads whereas the STM library cannot 
assume the conflicts will not occur within the methods and must restart transactions in these cases, degrading performance.

![alt text](https://github.com/ucfblythe/COP6616-LFCAT/blob/master/images/STM_throughput.png "STM Throughput")
