string = open('data.txt', 'r').read()
pos=string.find('l')
pos = pos - 1
print string[0:pos]
