.PHONY: test core travel-history travel travel-emulator schemas kotlin demo ci-actions doctor

test: core

core: ci-actions schemas kotlin

schemas:
	python3 scripts/validate-schemas.py

kotlin:
	./scripts/test-reference.sh

travel-history:
	sh tools/dna check-travel-history

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
