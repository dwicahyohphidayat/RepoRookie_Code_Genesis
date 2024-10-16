from flask import Flask, request
import requests
import threading
import os
import logging
import sys

app = Flask(__name__)
app.debug = os.environ.get('FLASK_DEBUG') == '0'
logging.basicConfig(stream=sys.stdout, level=logging.INFO)

# Fetch service URLs from environment variables or use default
service1_url = os.getenv('SERVICE1_URL', 'http://localhost:5001')
service2_url = os.getenv('SERVICE2_URL', 'http://localhost:5002')
log_body_request = os.getenv('LOG_BODY_REQUEST', 'false').lower() == 'true'

@app.route('/', defaults={'subpath': ''}, methods=['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'])
@app.route('/<path:subpath>', methods=['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'])
def catch_all(subpath):
    url1 = service1_url + ('/' + subpath if subpath else '')
    url2 = service2_url + ('/' + subpath if subpath else '')    
    method = request.method
    data = request.get_json() if request.method != 'GET' else None
    headers = dict(request.headers)
    
    threading.Thread(target=forward_request, args=(url1, method, data, headers)).start()
    threading.Thread(target=forward_request, args=(url2, method, data, headers)).start()
    
    return 'Request forwarded', 200

def forward_request(url, method, data, headers):
    try:
        if log_body_request:
            logging.info(f'Request to {url}: Method={method}, Headers={headers}, Body={data}')

        if method == 'POST':
            response = requests.post(url, json=data)
        elif method == 'PUT':
            response = requests.put(url, json=data)
        elif method == 'DELETE':
            response = requests.delete(url, json=data)
        elif method == 'PATCH':
            response = requests.patch(url, json=data)
        elif method == 'OPTIONS':
            response = requests.options(url)
        elif method == 'GET':
            response = requests.get(url)
        
        logging.info(f'Response from {url}: {response.text}')
    except requests.exceptions.RequestException as e:
        logging.error(f'Error forwarding request to {url}: {str(e)}')

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    app.run(host='0.0.0.0', port=port)
