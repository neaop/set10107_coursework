import subprocess

for i in range(1, 51):
    subprocess.run(
        ['java', '-jar', 'set10107_coursework.jar', 'A', '--sd', 'hidden-layer', '--hl', '{]'.format(i), '-s', 'RANDOM',
         '-c', 'SINGLE', '-i', '--pop', '500'])
