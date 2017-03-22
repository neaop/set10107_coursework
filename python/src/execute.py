import subprocess

for i in range(1, 50):
    subprocess.run(
        ['java', '-jar', 'set10107_coursework.jar', 'A', '-s', 'RANDOM', '-c', 'SINGLE', '--pop', '{}'.format(i * 10)])
