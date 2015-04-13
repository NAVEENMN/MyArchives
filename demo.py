from os.path import join
from xlutils.copy import copy
from xlrd import open_workbook
import os, sys
import urllib2
from firebase import firebase

look_up = dict()
look_up['Total Unique applicants'] = 0
look_up['total applicants'] = 0
look_up['repeated'] = 0
look_up['Male'] = 0
look_up['Female'] = 0
look_up['unspecified'] = 0
look_up['White'] = 0
look_up['Afro American'] = 0
look_up['Asian'] = 0
look_up['Hispanic/Latino'] = 0
look_up['others'] = 0
look_up['freshman'] = 0
look_up['junior'] = 0
look_up['sophomore'] = 0
look_up['senior'] = 0
look_up['universities'] = 0
look_up['listmales'] = 0
look_up['listfemales'] = 0
look_up['listunspecified'] = 0

fb = firebase.FirebaseApplication('https://cise.firebaseio.com/', None)
# This function takes care of setting up the workbook and returns boundaries
def setup_workbook(workbook):
	rb = open_workbook(workbook, formatting_info=False, on_demand=True )
	wb = copy(rb)
	p, q = find_boundries(rb)
	return p, q
		
#function used to find the boundries
def find_boundries(rb):
	sheet1 = rb.sheet_by_index(0)
	xcount = 0
	ycount = 0
	val = 0
	univ = list()
	malelist = list()
	femalelist = list()
	unspecifiedlist = list()
	# compute y boundry
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(ycount,0).value
		univ.append(val)
		malelist.append(rb.sheet_by_index(0).cell(ycount,4).value)
		femalelist.append(rb.sheet_by_index(0).cell(ycount,5).value)
		unspecifiedlist.append(rb.sheet_by_index(0).cell(ycount,6).value)
		ycount = ycount + 1

	look_up['universities'] = univ
	look_up['listmales'] = malelist
	look_up['listfemales'] = femalelist
	look_up['listunspecified'] = unspecifiedlist
	ycount = ycount - 1 
	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(0,xcount).value
		if val in look_up.keys():
			look_up[val] = rb.sheet_by_index(0).cell(ycount-1, xcount).value
		xcount = xcount + 1 
	xcount = xcount - 1
	val = 0

	return(xcount, ycount)	

def main():
	x_limit, y_limit = setup_workbook('sample.xls')
	# we have to put one by one unfortunatley fb api doesnt retain
	universities = look_up['universities']
	males = look_up['listmales']
	females = look_up['listfemales']
	unspecified = look_up['listunspecified']
	print look_up['listmales']
	fb.put("Totals","2014/universities",{"list" : universities[1:len(universities)-3]})
	fb.put("Totals","2014/MALES",{"listmales" : males[1:len(universities)-3]})
	fb.put("Totals","2014/FEMALES",{"listfemales" : females[1:len(universities)-3]})
	fb.put("Totals","2014/UNSPECIFIED",{"listunspecified" : unspecified[1:len(universities)-3]})

	fb.put("Totals","2014/total",{"total applicants" : look_up["total applicants"],
					 "Total Unique applicants" : look_up["Total Unique applicants"]})
	fb.put("Totals","2014/gender",{"Male" : look_up["Male"],
					  "unspecified" : look_up["unspecified"],
					  "Female" : look_up["Female"]})
	fb.put("Totals","2014/race",{"White" : look_up["White"],
					"Asian" : look_up["Asian"],
					"Hispanic" : look_up["Hispanic/Latino"],
					"others" : look_up["others"],
					"Afro American" : look_up["Afro American"]})
	fb.put("Totals","2014/level",{"sophomore" : look_up["sophomore"],
					"junior" : look_up["junior"],
					"freshman" : look_up["freshman"],
					"senior" : look_up["senior"]})

#-----------
	yer = ['2010','2011','2012','2013','2014']
	white =['61','52','52','53','46']	
	asia =['10','14','11','13','16']
	afr = ['7','10','17','12','10']
 	his = ['3','8','10','9','13']
    	fresh = ['86','64','146','201','233']
    	sop = ['191','264','444','665','1099']
    	jun = ['294','273','664','902','858']
   	sen = ['123','142','295','345','359']
    	mal = ['500','615','1156','1503','1867']
    	fem = ['187','220','380','593','711']

    	fb.put("Totals","combined/years",{"list" : yer})
    	fb.put("Totals","combined/males",{"listmales" : mal})
    	fb.put("Totals","combined/females",{"listfemales" : fem})
    	fb.put("Totals","combined/white",{"listwhite" : white})
    	fb.put("Totals","combined/afr",{"listafr" : afr})
    	fb.put("Totals","combined/asia",{"listasia" : asia})
    	fb.put("Totals","combined/his",{"listhis" : his})
	fb.put("Totals","combined/fresh",{"freshman" : fresh})
	fb.put("Totals","combined/sop",{"sophomore" : sop})
	fb.put("Totals","combined/jun",{"juniour" : jun})
	fb.put("Totals","combined/sen",{"seniour" : sen})

#-----------
if __name__ == "__main__" :
	main()
