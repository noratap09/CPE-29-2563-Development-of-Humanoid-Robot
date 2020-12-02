from flask import Flask, request
from flask_cors import CORS, cross_origin

import json
import base64
import cv2
import cv2.aruco as aruco
import  numpy as np

app = Flask(__name__)
CORS(app)

@app.route('/')
def helloworld():
	return 'helloworld'

@app.route('/show_img',methods=['GET'])
@cross_origin()
def show_img():
	img_base64 = request.values['img_base64']
	img_base64 = img_base64.replace(" ","+") 
	return "<img src='data:image/jpeg;base64,{0}'>".format(img_base64)


@app.route('/show_img_ar',methods=['GET'])
def show_img_ar():
	img_base64 = request.values['img_base64']
	mtx = request.values['mtx']
	dist = request.values['dist']
	maker_size = float(request.values['maker_size'])

	img_base64 = img_base64.replace(" ","+")
	mtx = np.array(json.loads(mtx))
	dist = np.array(json.loads(dist)) 

	img_base64_bytes = img_base64.encode('ascii')
	img_decode = base64.b64decode(img_base64_bytes)

	img_decode = np.asarray(bytearray(img_decode), dtype="uint8")
	img = cv2.imdecode(img_decode, cv2.IMREAD_COLOR)

	print(str(mtx),str(dist))

	#AR Detect
	aruco_dict = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)

	con, id, _ = aruco.detectMarkers(img, aruco_dict)

	if np.asarray(id).all() != None:
		for i in range(0,np.asarray(id).size):
			x_c = (max(con[i][0][0][0], con[i][0][1][0], con[i][0][2][0], con[i][0][3][0]) - min(con[i][0][0][0],\
								                                         con[i][0][1][0],\
								                                         con[i][0][2][0],\
								                                         con[i][0][3][0])) / 2
			y_c = (max(con[i][0][0][1], con[i][0][1][1], con[i][0][2][1], con[i][0][3][1]) - min(con[i][0][0][1],\
								                                         con[i][0][1][1],\
								                                         con[i][0][2][1],\
								                                         con[i][0][3][1])) / 2

			x_c = min(con[i][0][0][0], con[i][0][1][0], con[i][0][2][0], con[i][0][3][0]) + x_c
			y_c = min(con[i][0][0][1], con[i][0][1][1], con[i][0][2][1], con[i][0][3][1]) + y_c

			#cv2.circle(result_img, (int(x_c), int(y_c)), 10, (100, 100, 255), -1)
			ar_img = aruco.drawDetectedMarkers(img, con, id, (0, 0, 255))
			size_maker = maker_size # in meter
			rvecs , tvecs , trash =  aruco.estimatePoseSingleMarkers(con,size_maker,mtx,dist)
			print(rvecs.shape)
			print(trash)
			for j in range(0,rvecs.shape[0]):
				aruco.drawAxis(img, mtx, dist, rvecs[j], tvecs[j], 0.03)
				cv2.putText(img,"({0:.2f},{1:.2f},{2:.2f})".format(tvecs[j][0][0],tvecs[j][0][1],tvecs[j][0][2]),(con[i][0][0][0],con[i][0][0][1]),cv2.FONT_HERSHEY_SIMPLEX,0.4,(0,0,255))
			print("ID:", id[i]," x:",x_c," y:",y_c)
			print("DEGREE:",np.degrees(rvecs))
			print("POS:",tvecs)

	aruco.drawDetectedMarkers(img, con, id, (0, 0, 255))
	'''
	cv2.imshow("IMG",img)
	if cv2.waitKey(0) & 0xFF == ord('q'):
		cv2.destroyAllWindows()	
		return "<img src='data:image/jpeg;base64,{0}'>".format(img_base64)

	'''
	#display img
	retval , buffer = cv2.imencode('.jpg',img)
	img_base64_result = base64.b64encode(buffer).decode('utf-8')
	print("Result:",img_base64_result)	

	result = "{{ 'POS':{0},'DEG':{1} }}".format(tvecs.tolist(),tvecs.tolist())

	return "<img src='data:image/jpeg;base64,{0}'><hr><img src='data:image/jpeg;base64,{1}'><hr>{2}".format(img_base64,img_base64_result,result)
	

@app.route('/area',methods=['GET'])
@cross_origin()
def are():
	w = float(request.values['w'])
	h = float(request.values['h'])
	return str(w*h)
	
if __name__ == '__main__':
	app.run(host='0.0.0.0',port=5000,debug=False)
