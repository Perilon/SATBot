from nltk import *


gramfile='sem2.fcfg'
#g = '/home/vivian/nltk_data/grammars/book_grammars/discourse.fcfg'
inputs = ['Suzie chases a dog', 'every dog barks']
parses = parse_sents(inputs, gramfile)
for sent, trees in zip(inputs, parses):
	print()
	print("Sentence: %s" % sent)
	for tree in trees:
		print("Parse:\n %s" %tree)
		#print("Semantics: %s" %  root_semrep(tree)) #this fails, dunno why


