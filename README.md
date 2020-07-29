# MyArchives
Archives from my undergrad and grad school days.
Its only a matter of time your number of repo grows and a day comes where you need to do house cleaning.
You do not want to throw away your old code as it holds so many sweet and harsh memories. But, you want to clean up and organize your old codes as they can come handy when you want to lookup something.
This script should help you do that. It also preserves all commit histories and contributors.

## How it works.
Follow the script and you will understand how it works. 

## Usage.
- Create a new repo call it like MyArchives
- Save the following script and give it a name something like archivegit.sh.
```shell
#!/bin/bash
REPONAME=$2
TARGETREPONAME=$1
REPO=https://github.com/NAVEENMN/$REPONAME.git

echo "\nMerging $REPONAME to $TARGETREPONAME"
if [ -d "/tmp/gitarchive" ]
then
    rm -rf /tmp/gitarchive
fi

echo "\n====> Cloning MyArchives"
git clone https://github.com/NAVEENMN/MyArchives.git /tmp/gitarchive
cd /tmp/gitarchive
git remote -v

echo "\n====> Adding $REPO"
git remote add -f $REPONAME $REPO

echo "\n====> Fetching $REPO"
git fetch $REPO
git remote -v

echo "\n====> checking out $REPONAME/master"
git checkout -b migration "${REPONAME}/master"

# Remove files you dont need.
echo "\n====> Removing config files if they exist"
git rm -r .settings
git rm -r .classpath
git rm -r .project
git rm .DS_Store

echo "\n====> Moving all contents to directory $REPONAME"
for x in *; do
    if [ ! -d $REPONAME ]
    then
        mkdir $REPONAME
    fi
    git mv $x $REPONAME && echo "moved $x -> $REPONAME"
done

#echo "\n** Git status"
#echo "======="
#git status
#echo "======="

echo "\n====> Commiting and merging and pushing"
git commit -m "merging repo $REPONAME --> $TARGETREPONAME"
git checkout master
git merge migration --allow-unrelated-histories
git push origin master
```
- Run the script with arguments
```shell
sh archivegit.sh <source reponame> <target reponame>
```
For Example If you want to merge your old repo by name DijkstrasAlgorithm to our new repo which you just created MyArchives
```shell
sh archivegit.sh DijkstrasAlgorithm MyArchives
```

## Output
I understand running git scripts can be freaky!! so I have pasted my output. This is how it merges.

```shell
(base) Naveens-MBP:Projects naveenmysore$ sh archivegit.sh MyArchives DijkstrasAlgorithm

Merging DijkstrasAlgorithm to MyArchives

====> Cloning MyArchives
Cloning into '/tmp/gitarchive'...
remote: Enumerating objects: 18, done.
remote: Counting objects: 100% (18/18), done.
remote: Compressing objects: 100% (13/13), done.
remote: Total 24595 (delta 3), reused 16 (delta 3), pack-reused 24577
Receiving objects: 100% (24595/24595), 1.48 GiB | 20.48 MiB/s, done.
Resolving deltas: 100% (14557/14557), done.
Checking out files: 100% (8703/8703), done.
origin	https://github.com/NAVEENMN/MyArchives.git (fetch)
origin	https://github.com/NAVEENMN/MyArchives.git (push)

====> Adding https://github.com/NAVEENMN/DijkstrasAlgorithm.git
Updating DijkstrasAlgorithm
warning: no common commits
remote: Enumerating objects: 39, done.
remote: Total 39 (delta 0), reused 0 (delta 0), pack-reused 39
Unpacking objects: 100% (39/39), done.
From https://github.com/NAVEENMN/DijkstrasAlgorithm
 * [new branch]        master     -> DijkstrasAlgorithm/master

====> Fetching https://github.com/NAVEENMN/DijkstrasAlgorithm.git
From https://github.com/NAVEENMN/DijkstrasAlgorithm
 * branch              HEAD       -> FETCH_HEAD
DijkstrasAlgorithm	https://github.com/NAVEENMN/DijkstrasAlgorithm.git (fetch)
DijkstrasAlgorithm	https://github.com/NAVEENMN/DijkstrasAlgorithm.git (push)
origin	https://github.com/NAVEENMN/MyArchives.git (fetch)
origin	https://github.com/NAVEENMN/MyArchives.git (push)

====> checking out DijkstrasAlgorithm/master
Branch 'migration' set up to track remote branch 'master' from 'DijkstrasAlgorithm'.
  1 Merge branch 'migration'
Switched to a new branch 'migration'

====> Removing config files if they exist
fatal: pathspec '.settings' did not match any files
fatal: pathspec '.classpath' did not match any files
fatal: pathspec '.project' did not match any files
fatal: pathspec '.DS_Store' did not match any files

====> Moving all contents to directory DijkstrasAlgorithm
moved Mysore_Naveen-Katti_Apoorva.pdf -> DijkstrasAlgorithm
moved README -> DijkstrasAlgorithm
moved README~ -> DijkstrasAlgorithm
moved data.txt -> DijkstrasAlgorithm
moved data.txt~ -> DijkstrasAlgorithm
moved graph.py -> DijkstrasAlgorithm
moved graph.py~ -> DijkstrasAlgorithm
moved krushal.py -> DijkstrasAlgorithm
moved krushal.py~ -> DijkstrasAlgorithm
moved krushkal -> DijkstrasAlgorithm
moved priodict.py -> DijkstrasAlgorithm
moved priodict.pyc -> DijkstrasAlgorithm

====> Commiting and merging and pushing
[migration 09e44bf2] merging repo DijkstrasAlgorithm --> MyArchives
 12 files changed, 0 insertions(+), 0 deletions(-)
 rename Mysore_Naveen-Katti_Apoorva.pdf => DijkstrasAlgorithm/Mysore_Naveen-Katti_Apoorva.pdf (100%)
 rename README => DijkstrasAlgorithm/README (100%)
 rename README~ => DijkstrasAlgorithm/README~ (100%)
 rename data.txt => DijkstrasAlgorithm/data.txt (100%)
 rename data.txt~ => DijkstrasAlgorithm/data.txt~ (100%)
 rename graph.py => DijkstrasAlgorithm/graph.py (100%)
 rename graph.py~ => DijkstrasAlgorithm/graph.py~ (100%)
 rename krushal.py => DijkstrasAlgorithm/krushal.py (100%)
 rename krushal.py~ => DijkstrasAlgorithm/krushal.py~ (100%)
 rename {krushkal => DijkstrasAlgorithm/krushkal}/krushal.py (100%)
 rename priodict.py => DijkstrasAlgorithm/priodict.py (100%)
 rename priodict.pyc => DijkstrasAlgorithm/priodict.pyc (100%)
Checking out files: 100% (8715/8715), done.
Switched to branch 'master'
Your branch is up to date with 'origin/master'.
Merge made by the 'recursive' strategy.
 DijkstrasAlgorithm/Mysore_Naveen-Katti_Apoorva.pdf | Bin 0 -> 22095 bytes
 DijkstrasAlgorithm/README                          | 108 +++++++++++++++++++++
 DijkstrasAlgorithm/README~                         | 112 +++++++++++++++++++++
 DijkstrasAlgorithm/data.txt                        |  10 ++
 DijkstrasAlgorithm/data.txt~                       |  11 +++
 DijkstrasAlgorithm/graph.py                        | 220 ++++++++++++++++++++++++++++++++++++++++++
 DijkstrasAlgorithm/graph.py~                       | 220 ++++++++++++++++++++++++++++++++++++++++++
 DijkstrasAlgorithm/krushal.py                      |  82 ++++++++++++++++
 DijkstrasAlgorithm/krushal.py~                     |  82 ++++++++++++++++
 DijkstrasAlgorithm/krushkal/krushal.py             |  49 ++++++++++
 DijkstrasAlgorithm/priodict.py                     |  71 ++++++++++++++
 DijkstrasAlgorithm/priodict.pyc                    | Bin 0 -> 2649 bytes
 12 files changed, 965 insertions(+)
 create mode 100644 DijkstrasAlgorithm/Mysore_Naveen-Katti_Apoorva.pdf
 create mode 100644 DijkstrasAlgorithm/README
 create mode 100644 DijkstrasAlgorithm/README~
 create mode 100644 DijkstrasAlgorithm/data.txt
 create mode 100644 DijkstrasAlgorithm/data.txt~
 create mode 100644 DijkstrasAlgorithm/graph.py
 create mode 100644 DijkstrasAlgorithm/graph.py~
 create mode 100644 DijkstrasAlgorithm/krushal.py
 create mode 100644 DijkstrasAlgorithm/krushal.py~
 create mode 100644 DijkstrasAlgorithm/krushkal/krushal.py
 create mode 100644 DijkstrasAlgorithm/priodict.py
 create mode 100644 DijkstrasAlgorithm/priodict.pyc
Enumerating objects: 44, done.
Counting objects: 100% (44/44), done.
Delta compression using up to 8 threads
Compressing objects: 100% (41/41), done.
Writing objects: 100% (43/43), 29.97 KiB | 9.99 MiB/s, done.
Total 43 (delta 24), reused 0 (delta 0)
remote: Resolving deltas: 100% (24/24), completed with 1 local object.
To https://github.com/NAVEENMN/MyArchives.git
   bc40715f..4627e051  master -> master
(base) Naveens-MBP:Projects naveenmysore$
```
