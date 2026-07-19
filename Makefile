.PHONY: test core travel travel-emulator schemas kotlin demo ci-actions doctor

test: core

core: schemas

schemas:
	python3 scripts/validate-schemas.py

kotlin:
	./scripts/test-reference.sh

travel:
	sh tools/dna check-travel

travel-emulator:
	sh tools/dna check-travel-emulator

ci-actions:
	sh tools/dna check-ci-actions

doctor:
	sh tools/dna doctor

demo:
	./scripts/run-demo.sh
