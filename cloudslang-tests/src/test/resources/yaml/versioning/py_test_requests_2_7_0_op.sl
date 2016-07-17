namespace: user.versioning.ops

operation:
  name: py_test_requests_2_7_0_op
  inputs:
    - url:
        required: true
  python_action:
    dependencies:
      - 'requests == 2.7.0'
    script: |
      import requests
      r = requests.get(url)
      r_code = r.status_code
      r_text = r.text
      r_headers = r.headers['content-type']
  outputs:
    - request_code: ${r_code}
    - request_headers: ${r_headers}
    - request_text: ${r_text}
  results:
    - SUCCESS

