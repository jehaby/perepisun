.PHONY: \
	build \
	deploy \
	upload \
	setup-dev-webhook

build:
	clojure -X:uberjar

upload:
	scp rewraktar.jar vps1:rewraktar/

restart:
	ssh vps1 'systemctl restart rewraktar'

deploy: build upload restart

setup-dev-webhook:
	curl --header "Content-Type: application/json" \
        --data-raw "{\"url\":\"${WEBHOOK_CALLBACK_URI}/api/foo\"}" \
        -X POST "https://api.telegram.org/bot${BOT_API_KEY}/setWebhook"
