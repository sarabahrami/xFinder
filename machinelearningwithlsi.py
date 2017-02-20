#Author:Sara Baahrami
from gensim import corpora, models, similarities
import os
import sys
path2='/home/sunshine40270/mine/projectdata/extensionwork'
#path2='/data/sara/EclipseExperiment'
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
#creating term frequency matrix for dictionary
tfidf=models.TfidfModel(corpus)
corpus_tfidf=tfidf[corpus]
lsi=models.LsiModel(corpus_tfidf,id2word=dictionary,num_topics=500)
corpus_lsi=lsi[corpus_tfidf]
testdesc=sys.argv[1]
print testdesc
cr=dictionary.doc2bow(testdesc.lower().split())
vec_lsi=lsi[tfidf[cr]]
index = similarities.docsim.MatrixSimilarity(corpus_lsi,chunksize=1024)
sim= index[vec_lsi]
list= sorted(enumerate(sim), key=lambda item: -item[1])
#print list
#number of top selected similar past bug report
max=10
top=list[ :max]
file1= open (os.path.join(path2,Corpusfilename),'r')
lines=file1.readlines()
listoffile=[[0 for x in xrange(1)]for x in xrange(max)]
i=0
for x in top:
    d=x[0]
    f=lines[d]
    filename=f.split("\t",2)[0]
    #print filename
    #print'............................'
    desc=f.split()
    listoffile[i]=filename
    i=i+1
file1.close()

file3= open (os.path.join(path2,resultfilename),'w')
            
for item in listoffile:
    file3.write(item)
    file3.write("\n")
file3.close()
