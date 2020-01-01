#!/bin/python3

email = 'my@email.com'
password = 'password_for_CodinGame'
program_execute = 'mono NumberShifting.exe'


import io
import requests
import subprocess

# login to CodinGame and get submit ID
session = requests.Session()
session.post('https://www.codingame.com/services/Codingamer/loginSiteV2', json=[email, password, True])

# TODO when approved:
# use https://www.codingame.com/services/Puzzle/generateSessionFromPuzzlePrettyId
r = session.post('https://www.codingame.com/services/PredefinedTest/generateSessionFromTestHandle', json=['8344035e21eef48e0b0973bf53304b1122ee76'])
handle = r.json()['handle']

# for each level of the game
while True:
	# run the solver on level.txt and save output to solution.txt
	subprocess.run(program_execute + " < level.txt > solution.txt", shell=True)
	with open('level_password.txt', 'r') as f:
		level_pass = f.read().strip()
	with open('solution.txt', 'r') as f:
		solution = f.read().strip()
	solution = level_pass + '\n' + solution
	with open('log.txt', 'a') as f:
		f.write('\nsolution:\n')
		f.write(solution)
	
	# submit the solution to CodinGame
	r = session.post('https://www.codingame.com/services/TestSession/play', json=[handle, {'code':solution, 'programmingLanguageId':'PHP', 'multipleLanguages':{'testIndex':1}}])
	print('replay: https://www.codingame.com/replay/' + str(r.json()['gameId']))
	next_level = ''
	if 'gameInformation' in r.json()['frames'][-1]:
		next_level = r.json()['frames'][-1]['gameInformation']
	if not 'Code for next level' in next_level:
		print('The solution was wrong, watch the replay for details')
		break
	next_level = next_level[next_level.find(':')+2:]

	# save input for next level
	with open('level_password.txt', 'w') as f:
		f.write(next_level.split('\n')[0])
	with open('level.txt', 'w') as f:
		f.write('\n'.join(next_level.split('\n')[1:]))
	with open('log.txt', 'a') as f:
		f.write('\nreplay: https://www.codingame.com/replay/' + str(r.json()['gameId']))
		f.write('\n\nLevel ' + str(int(1 + r.json()['metadata']['Level'])) + ':\n')
		f.write(next_level)
