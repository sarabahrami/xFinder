#Author:Sara Bahrami
#Textual similarity between corpus and bug description by using Gensim.similarities.docsim.Similarity
from gensim import corpora, models, similarities
import os
import sys
import logging
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
path2='/home/sunshine40270/mine/projectdata/extensionwork'
#path2='/data/sara'
resultfilename=sys.argv[2]
Corpusfilename=sys.argv[3]
Corpuswithoutidfilename=sys.argv[4]
filewithid=open (os.path.join(path2,Corpusfilename),'r')
filewithoutid=open (os.path.join(path2,Corpuswithoutidfilename),'w')
for line in filewithid:
    s=line.split("\t")
    i=1
    while(i<=len(s)-1):
        filewithoutid.write(s[i])
        filewithoutid.write(" ")
        i=i+1
filewithid.close()
filewithoutid.close()

#def Predictioncombine(bug,path,path2,inputtrainfn1,inputtrainfn2,nm,max):
#creating dictionary from all of bug description
texts=[[word for word in line.lower().split()]for line in open(os.path.join(path2,Corpuswithoutidfilename))]
dictionary=corpora.Dictionary(texts)
#creating corpus from all of the word in the dictionary
corpus =[dictionary.doc2bow(text) for text in texts]
testdesc=sys.argv[1]
print testdesc
cr=dictionary.doc2bow(testdesc.lower().split())
index = similarities.docsim.Similarity('/home/sunshine40270/mine/projectdata/extensionwork/tmp/tst',corpus,num_features=150000,num_best=10,chunksize=256,shardsize=32768)
sim= index[cr]
file1= open (os.path.join(path2,Corpusfilename),'r')
lines=file1.readlines()
listoffile=[[0 for x in xrange(1)]for x in xrange(10)]
i=0
for idx,val in enumerate(sim):
    d=val[0]
    f=lines[d]
    filename=f.split("\t",2)[0]
    desc=f.split()
    listoffile[i]=filename
    i=i+1
file1.close()

file3= open (os.path.join(path2,resultfilename),'w')
            
for item in listoffile:
    file3.write(item)
    file3.write("\n")
file3.close()
