build:
	make -C app build

test:
	make -C app test

checkstyleMain:
	make -C app checkstyleMain

report:
	make -C app report

run-dist:
	make -C app run-dist