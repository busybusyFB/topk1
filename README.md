# H1B_Statistics
Solution to Insight data engineer code challenge implemented in Java: Calculating Top 10 states and occupations for certified H1B applicants.

## Table of contents
1. [Problem](README.md##Problem)
2. [Approach](README.md##Approach)
3. [Run instructions](README.md##Run-instructions)
4. [Alternative approaches](README.md##Alternative-approaches)

## Problem

See the detailed description in the [Github repo of InsightDataScience](https://github.com/InsightDataScience/h1b_statistics).

## Approach
**HashMap + Priority queue**

Step 1: Read H1B cases line by line from raw data, compute the counts of states and occupations of each certified H1B applications and save them in two hash maps (one for states, the other for occupations). the keys are state or occupation names, and the values are the corresponding frequencies.

Step 2: Use minHeaps with maximum size of 10. The ordering is primarily based on the frequencies of each state and occupation. If frequecies are the same, the ordering is then based on the lexical order of state or occupation name.

Step 3: Iterative over the entrys of the two hash maps and add each of them into the corresponding minHeaps. If the size of a minHeap exceeds 10, delete the mininum entry in the minHeap.

Step 4: Convert the minHeaps to lists and reverse the lists. The reversed list stores the top 10 states and occupations.

**Performance of this approach**

Define:

-The number of entries as `n`

-The number of unique keys in the hash map as `m`

-The number of targets as `k`

Time complexity is `O(n + mlogk)`

Space compleisty is `O(m + k)`

## Run instructions

## Alternative approaches
