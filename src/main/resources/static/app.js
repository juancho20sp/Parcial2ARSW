

//local player data
var mycar=undefined;
var mycarxpos=10;
var mycarypos=10;


//competitors data
var loadedCars=[];
var carsCurrentXPositions=[];
var carsCurrentYPositions=[];


var stompClient = null;

movex = function(){    
    mycarxpos+=10;
    paintCars();

    let myCar = loadedCars.filter(car => car.number ===  mycar.number)[0];
    myCar = {
        ...myCar,
        xPos: myCar.xPos + 10
    }

    if (myCar.xPos >= 640) {
        $.ajax({
            url: "races/25/winner",
            type: 'POST',
            data: JSON.stringify({number: myCar.number}),
            contentType: "application/json"
        }).then(
                function(data){ 
                    stompClient.send("/topic/winner", {}, JSON.stringify({myCar})); 
                },
                function(err){
                    alert("err:"+err.responseText);
                }
                     
        );
    }


    stompClient.send("/topic/car"+mycar.number, {}, JSON.stringify({myCar})); 
};


initAndRegisterInServer = function(){
    mycar={number:Number($("#playerid").val())};
    mycarxpos=10;
    mycarypos=10;

    carsCurrentXPositions[mycar.number] = mycarxpos;
    carsCurrentYPositions[mycar.number] = mycarypos;
        
    $.ajax({
        url: "races/25/participants",
        type: 'PUT',
        data: JSON.stringify(mycar),
        contentType: "application/json"
    }).then(
            function(){                
                alert("Competitor registered successfully!");
                paintCars();

                loadCompetitorsFromServer();
            },
            function(err){
                alert("err:"+err.responseText);
            }
                 
    );
};

loadCompetitorsFromServer = function () {
    if (mycar == undefined) {
        alert('Register your car first!');
    } else {
        $.get("races/25/participants",
                function (data) {
                    loadedCars = data;

                    if (loadedCars.length === 5){
                        $("#register").prop('disabled', true); 
                    }

                    loadedCars = loadedCars.map((car, idx) => {
                        return {
                            number: car.number,
                            xPos: 10,
                            yPos: 40 * (idx + 1)
                        }
                    })

                // Notify everyone that a new car is created
                stompClient.send("/topic/totalCars", {}, JSON.stringify({
                    loadedCars,
                    carsCurrentXPositions,
                    carsCurrentYPositions
                })); 

                    paintCars();
                }
        );
    }

};

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

paintCars=function(){
    canvas=$("#cnv")[0];
    ctx=canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    //paint competitors cars
    loadedCars.forEach(
            function(car){
                const {xPos, yPos} = car;
                paint(car,xPos,yPos,ctx);

            }
    );
};


paint=function(car,xposition,yposition,ctx){
    
    var img = new Image;
    img.src = "img/car.png";
    img.onload = function(){        
        ctx.drawImage(img,xposition,yposition); 
        ctx.fillStyle = "white";
        ctx.font = "bold 16px Arial";
        ctx.fillText(""+car.number, xposition+(img.naturalWidth/2), yposition+(img.naturalHeight/2));        
    };    
};



$(document).ready(
        
        function () {
            console.info('loading script!...');
            $(".controls").prop('disabled', false);    
            $("#racenum").prop('disabled', true); 
            
            // Disable 'MOVE MY CAR!' button by default
            $("#movebutton").prop('disabled', true); 
            document.querySelector('#movebutton').style.background = 'gray';

            var socket = new SockJS('/stompendpoint');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);


                // Subscribe to total cars
                stompClient.subscribe('/topic/totalCars', (data) => {
                    console.log(data);
                    console.log('new car added');

                    loadedCars = JSON.parse(data.body).loadedCars;
                    carsCurrentXPositions = JSON.parse(data.body).carsCurrentXPositions;
                    carsCurrentYPositions = JSON.parse(data.body).carsCurrentYPositions;

                    loadedCars.forEach(                        
                        function (car) {
                            stompClient.subscribe('/topic/car'+car.number, function (data) {
                                msgdata=JSON.parse(data.body);
                                carsCurrentXPositions[msgdata.car]=msgdata.xpos; 

                                const carMovedIdx = loadedCars.findIndex(car => car.number === msgdata.myCar.number);  

                                loadedCars[carMovedIdx] = {
                                    ...loadedCars[carMovedIdx],
                                    ...msgdata.myCar
                                }

                                

                                paintCars();
                            });                              
                        }
                    );

                    paintCars();

                    // Enable the 'MOVE MY CAR!' button
                    if (loadedCars.length === 5){
                        $("#movebutton").prop('disabled', false); 
                        document.querySelector('#movebutton').style.background = '';
                    }
                });

                // Show winner in real time
                stompClient.subscribe('/topic/winner', data => {
                    const myData = JSON.parse(data.body);

                    

                    $.get("races/25/winner",
                            function (data) {
                                alert(`WE HAVE A WINNEEEEER: Vehicle #${data.number}`);

                                document.querySelector('#winner').innerHTML = `<p>Vehicle #${data.number}</p>`
                            }
                    );
                })
            });
        }
);

