$(document).ready(function(){
	
	
/* for the autocomplete data */	
var availableTags = [""];

$.ajax({
    url: 'Conn_add_new',
    data: {
        postVariableName: "gettingTheTitleAutocomplete",
    },
    success:function(data){
    	$("#contents-for-autocomplete").html(data);
    	$('.doc-title-value').each( function(){
    		availableTags.push($(this).text());
    	});
    	$('.doc-title-value').remove();
    },
    type: 'POST'
});
/* end for the autocomplete data */	

/* main search function*/
function postSearchFunction(){
	$("#no-result-found").hide();
	var search_text = $("#search-text-box").val();
	search_text=search_text.trim().toLowerCase();
	$("#search-title-span").text(search_text);
  if(search_text == "")
  {
    $(".search-title").hide();
    $("#topic-list").html("");
  }
else
  {
    $(".search-title").show();
	
	$.ajax({
	    url: 'Conn_add_new',
	    data: {
	        postVariableName: "gettingTheDoc",
	        search_text: search_text
	    },
	    success:function(data){
	    	$("#topic-list").html(data);
	    	if($("#no-of-doc").text().trim()=="0")
	    	{
	    		$("#no-result-found").show();
	    	}
	    },
	    type: 'POST'
	});

  }
}	
	
	

$('.search-term').keydown(function(e) {
      if (e.keyCode == 13) {
    postSearchFunction();
    }
 });

$("#dell-search-button").click(function(){
  postSearchFunction();
});

$(document).on('click', '.doc_update_button', function(){
	$(".add-new-div").hide();
	$(".update-div #title").val($(this).next("span").text().trim());
	$(".update-div #short_desc").val($(this).next("span").next("span").text().trim());
	$(".update-div #long_desc").val($(this).parent().parent().next("p").text().trim());
	$(".update-div").show();
});

$("#update-form-close").click(function(){
	$(".update-div").hide();
	});

$("#add-new-form-close").click(function(){
	$(".add-new-div").hide();
	});

$("#add_new_button").click(function(){
	$(".update-div").hide();
	$(".add-new-div").show();
	});


  $(function() {
    $( "#search-text-box,.add-new-div .input_title" ).autocomplete({
      source: availableTags
    });
  });

  /*for the acknowledgement for the task eq-adding,updating*/
	var add_result=$(".add-result").text().trim();
	if(add_result.search("44")  > -1 ){
		alert("successfully done...");
	}
	else if(add_result.search("55")  > -1 ){
		alert("sorry!! Title is not available...");
	}
	else if(add_result.search("66")  > -1 ){
		alert("Failed!! Title is already present! Please update...");
	}
	
/*for storing the file in es*/
    var file_value="";
    var handleFileSelect = function(evt) {
    var files = evt.target.files;
    var file = files[0];
    if (files && file) {
        var reader = new FileReader();

        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            file_value = btoa(binaryString);
        };

        reader.readAsBinaryString(file);
    }
   
};

if (window.File && window.FileReader && window.FileList && window.Blob) {
            var el = document.querySelectorAll('.input_file-upload');
            for(var i=0; i < el.length; i++){
                el[i].addEventListener('change', handleFileSelect, false);
            }
} else {
    alert('The File APIs are not fully supported in this browser.');
}

$('.input_file-upload').on('change',function(){
    var fileName = $(this).val();
    fileName = fileName.replace(/.*(\/|\\)/, '');
    $('.input_file-detail').val(fileName);
    $(".loading-anime-image").show();
    setTimeout(function () { $('.file-contents-stream').val(file_value); setTimeout(function () { $(".loading-anime-image").hide(); }, 500);}, 1000);
    
   
});

	
});

