# Cleanup
rm build -R
rm dist -R
rm twister2.egg-info -R

# Creating distribution
python3 setup.py bdist_wheel

# Installing Locally
python3 -m pip install dist/twister2-0.2.4-py3-none-any.whl --user --upgrade
#pip install dist/twister2-0.2.3-py3-none-any.whl --user

# Uploading to PIP
python3 -m twine upload dist/*
