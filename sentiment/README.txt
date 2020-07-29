This is the implementation of Sentiment Analysis and Sentimen Training using Map Reduce for the seeding dataset provided by Rotten Tomatoes movie review.
------------------ Input --------------------
Place your train.tsv (training dataset provided) files in /input directory
------------------ Java run configurations ----
Arguments : [inputpath] [outputpath] [intermediate_output_path] [review.txt path]
example: /home/nmysore/Documents/pr/sen/input/ /home/nmysore/Documents/pr/sen/outputs/output /home/nmysore/Documents/pr/sen/outputs/output_post
/home/nmysore/Documents/pr/sen/review.txt
----------------------------------------------
Run the MainDriver class
-----------------
information about different files used:
-------------------
Architecture implement for analysis
We first promt the user to enter the review and we store the review in review.txt. We then
execute the python script to clean up the input and extract bi words and uni words and save it back in
review.txt in this format (this movie –> movie-->good-->is good). We now move on to bucketing
process where index.java will take input from train.tsv and creates five buckets. We next execute
process.java which takes input from the buckets generated before and arranges them in an order for
sentiment calculation. process.java takes review.txt as an input along with buckets and creates a set of
new bucket files (0.txt, 1.txt, 2.txt...) which becomes input for next stage. gen_rank.java will take these
new bucket files and calculated the sentiment value. update_train.java creates a new file called inter.txt
which will hold all the words which were new i.e., the words which the user entered but not present in
train.tsv.
We now move on to training part. We take inputs from new bucket files and the list of words
which are not in the train.tsv but was involved in the user`s review. We take sentiment value calculated
and list of words in inter.txt as an input here. We now move on to update_sentiment.java which will
update sentiment values for the words which are already present in pool.txt and for words which are not
present in pool.txt we just enter the new words followed up current rank
----------------------

Sample Output
--------------------
------------------------------
----- Sentiment Analysis -----
Scale: 0-very negative, 1-negative, 2-neutral, 3-positive, 4-very positive
Review: This movie is awesome. The comedy involved is very subtle and keeps the audience engaging.
Sentiment Value: 3.3333333333333335
------------------------------
Words and their contribution: {subtle and=3.0, engaging=4.0, is=2.0, audience=2.0, comedy=3.0, the
audience=2.0, movie=2.0, The comedy=3.0, the=2.0, .=2.0, This=2.0, subtle=3.0, and=2.0,
involved=2.0, This movie=2.0, engaging .=4.0, very=2.0, keeps=2.0}
Bi grams involved: [The comedy, This movie, engaging ., subtle and, the audience]
Uni grams involved: [., This, and, audience, comedy, engaging, involved, is, keeps, movie, subtle, the,
very]
words contributed: [subtle and, The comedy, engaging .]
