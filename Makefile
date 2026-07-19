.PHONY: test schemas kotlin demo

test: kotlin schemas

kotlin:
	./scripts/test-reference.sh

schemas:
	python3 scripts/validate-schemas.py

demo:
	./scripts/run-demo.sh
