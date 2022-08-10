.PHONY: \
	uberjar \
	deploy \
	upload \
	test \
	setup-dev-webhook

uberjar:
	TAOENSSO_TIMBRE_MIN_LEVEL_EDN=:warn clojure -X:uberjar

upload:
	scp rewraktar.jar vps1:rewraktar/

restart:
	ssh vps1 'systemctl restart rewraktar'

deploy: uberjar upload restart

test:
	TIMBRE_LEVEL=:warn neil test

setup-dev-webhook:
	curl --header "Content-Type: application/json" \
        --data-raw "{\"url\":\"${WEBHOOK_CALLBACK_URI}/api/foo\"}" \
        -X POST "https://api.telegram.org/bot${BOT_API_KEY}/setWebhook"
