#!/usr/bin/env python3

program_execute = 'mono NumberShifting.exe'

import os
import io
import requests
import subprocess

#put your email on a text file named cg_email.txt
with open('cg_email.txt', 'r') as f:
	email = f.read().strip()
#put your password on a text file named cg_pass.txt
with open('cg_pass.txt', 'r') as f:
	password = f.read().strip()

# login to CodinGame and get submit ID
session = requests.Session()
r = session.post('https://www.codingame.com/services/Codingamer/loginSiteV2', json=[email, password, True])
userId = r.json()['codinGamer']['userId']

r = session.post('https://www.codingame.com/services/Puzzle/generateSessionFromPuzzlePrettyId', json=[userId, "number-shifting", False])
handle = r.json()['handle']

# for each level of the game
while True:
	# run the solver on level.txt and save output to solution.txt
	# for Windows: set shell=False, if solution file isn't updated
	subprocess.run(program_execute + " < level.txt > solution.txt", shell=True)
	with open('level_password.txt', 'r') as f:
		level_pass = f.read().strip()
	with open('solution.txt', 'r') as f:
		solution = f.read().strip()
	if solution == '':
		print('Empty solution, crashed? ...')
		break
	solution = level_pass + '\n' + solution
	with open('log.txt', 'a') as f:
		f.write('\nsolution:\n')
		f.write(solution)
	
	# submit the solution to CodinGame
	r = session.post('https://www.codingame.com/services/TestSession/play', json=[handle, {'code':solution, 'programmingLanguageId':'PHP', 'multipleLanguages':{'testIndex':1}}])
	print('replay: https://www.codingame.com/replay/' + str(r.json()['gameId']))
	next_level = ''
	if 'gameInformation' in r.json()['frames'][-2]:
		next_level = r.json()['frames'][-2]['gameInformation']
	if not 'Code for next level' in next_level:
		print('The solution was wrong, watch the replay for details')
		break
	next_level = next_level[next_level.find(':')+2:]
	level_password = next_level.split('\n')[0]
	number_level = int(1 + r.json()['metadata']['Level'])
	with open('level_password.txt', 'w') as f:
		f.write(level_password)
	with open('number_level.txt', 'w') as f:
		f.write(str(number_level))
	# get the full level
	level_input='\n'.join(next_level.split('\n')[1:])
	if (number_level > 258): #fix for CG stderr limitations
		r = session.post('https://www.codingame.com/services/TestSession/play', json=[handle, {'code':'echo "'+level_password+'";cat >&2', 'programmingLanguageId':'Bash', 'multipleLanguages':{'testIndex':1}}])
		level_input = r.json()['frames'][2]['stderr']
	with open('level.txt', 'w') as f:
		f.write(level_input + '\n')
	
	# save input for next level
	with open('log.txt', 'a') as f:
		f.write('\nreplay: https://www.codingame.com/replay/' + str(r.json()['gameId']))
		f.write('\n\nLevel ' + str(number_level) + ':\n')
		f.write(level_password + '\n')
		f.write(level_input)
