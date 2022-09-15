(function() { // avoid variables ending up in the global scope

  // page components
  var albumDetails, albumsList,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
    pageOrchestrator.start(); // initialize the components
    pageOrchestrator.refresh(); // display initial content
  }, false);


  // Constructors of view components

  function PersonalMessage(_username, messagecontainer) {
    this.username = _username;
    this.show = function() {
      messagecontainer.textContent = this.username;
    }
  }

  function AlbumsList(_alert, _listcontainer, _listcontainerbody) {
    this.alert = _alert;
    this.listcontainer = _listcontainer;
    this.listcontainerbody = _listcontainerbody;

    var saveAlbumsOrderButton = document.getElementById("save-albums-order");

    $( ".row_drag" ).sortable({
      delay: 100,
      stop: function() {
        saveAlbumsOrderButton.style.visibility = "visible";
      }
    });

    this.reset = function() {
      this.listcontainer.style.visibility = "hidden";
      saveAlbumsOrderButton.style.visibility = "hidden";
    }

    this.show = function() {
      saveAlbumsOrderButton.style.visibility = "hidden";
      var self = this;
      makeCall("GET", "GetAlbumsList", null,
        function(req) {
          if (req.readyState === 4) {
            var message = req.responseText;
            if (req.status === 200) {
              self.update(JSON.parse(req.responseText)); // self visible by closure
            } else {
              self.alert.textContent = message;
            }
          }
        }
      );
    };

    this.update = function(arrayAlbums) {
      var l = arrayAlbums.length,
        row, titlecell, datecell, linkcell, anchor;
      if (l === 0) {
        alert.textContent = "No albums yet!";
      } else {
        this.listcontainerbody.innerHTML = ""; // empty the table body
        // build updated list
        var self = this;
        arrayAlbums.forEach(function(album) { // self visible here, not this
          row = document.createElement("tr");
          row.setAttribute("albumid", album.id);
          titlecell = document.createElement("td");
          titlecell.textContent = album.name;
          row.appendChild(titlecell);
          datecell = document.createElement("td");
          datecell.textContent = album.date;
          row.appendChild(datecell);
          linkcell = document.createElement("td");
          anchor = document.createElement("a");
          anchor.className += "link hover-link";
          linkcell.appendChild(anchor);
          linkText = document.createTextNode("Show");
          anchor.appendChild(linkText);
          anchor.setAttribute('albumid', album.id); // set a custom HTML attribute
          anchor.addEventListener("click", (e) => {
            // dependency via module parameter
            albumDetails.show(e.target.getAttribute("albumid")); // the list must know the details container
          }, false);
          anchor.href = "#";
          row.appendChild(linkcell);
          self.listcontainerbody.appendChild(row);
        });
        this.listcontainer.style.visibility = "visible";
      }
    }

    saveAlbumsOrderButton.addEventListener('click', (e) => {
      var rows = document.getElementById("id_listcontainerbody").rows;
      var i, albumsOrder = [];
      for (i = 0; i < rows.length; i++) {
        var albumId =  parseInt(rows[i].getAttribute("albumid"));
        if(!isNaN(albumId)) {
          albumsOrder.push(albumId);
        }

      }
      var elem = {}
      elem["order"] = albumsOrder;
      var self = this;
      postObjAsJson("POST", 'SaveAlbumsOrder', elem,
          function(req) {
            if (req.readyState === 4) {
              var message = req.responseText;
              if (req.status === 200) {
                saveAlbumsOrderButton.style.visibility = "hidden";
              } else {
                self.alert.textContent = message;
              }
            }
          }
      );
    });

  }

  function AlbumDetails(options) {
    this.alert = options['alert'];
    this.imagescontainer = options['imagescontainer']
    this.imagescontainerbody = options['imagescontainerbody'];
    this.imagedetailscontainer = options['imagedetailscontainer'];
    this.commentform = options['commentform'];
    var chunkedAlbum, nextButton, previousButton;

    // init buttons
    previousButton = document.createElement('input');
    previousButton.type = "button";
    previousButton.className = "btn";
    previousButton.id = "id_buttonprev";
    previousButton.value = "Previous";

    previousButton.addEventListener('click', (e) => {
      var groupId =  parseInt(previousButton.getAttribute('groupId'));
      if(!isNaN(groupId)) {
        this.update(groupId)
      }
    });

    nextButton = document.createElement('input');
    nextButton.type = "button";
    nextButton.className = "btn";
    nextButton.id = "id_buttonnext";
    nextButton.value = "Next";

    nextButton.addEventListener('click', (e) => {
      var groupId = parseInt(nextButton.getAttribute('groupId'));
      if(!isNaN(groupId)) {
        this.update(groupId)
      }
    });

    // restore custom validity when user is writing a comment
    this.commentform.querySelector("input[name='comment']").addEventListener('keyup', (e) => {
      e.target.setCustomValidity('');
    });

    this.commentform.querySelector("input[type='button']").addEventListener('click', (e) => {
      var form = e.target.closest("form");
      const isEmpty = str => !str.trim().length;
      var commentField = form.elements["comment"];
      if (form.checkValidity() && !isEmpty(commentField.value)) {
        var self = this,
         //form = event.target.closest("form"),
         imageIdForComment = form.querySelector("input[name='imageid']").value;
        makeCall("POST", 'AddComment', form,
          function(req) {
            if (req.readyState === 4) {
              var message = req.responseText;
              if (req.status === 200) {
                var comment = JSON.parse(req.responseText);
                self.addNewComment(comment, imageIdForComment)
              } else {
                self.alert.textContent = message;
              }
            }
          }
        );
      } else {
        if(isEmpty(commentField.value)) {
          commentField.setCustomValidity("Comment can not be empty");
        }
        form.reportValidity();
      }
    });



    this.addNewComment = function(comment, imageId) {
      // update the comments shown
      var currentIdShown = this.commentform.querySelector("input[name='imageid']").value;
      if(currentIdShown === imageId) { // check if image shown has not been changed
        var table = this.imagedetailscontainer.querySelector('.commentsdetails .comments-table');
        var row = document.createElement("tr")
        var usernameCell = document.createElement("th")
        usernameCell.textContent = 'Comment by ' + comment.username;
        row.appendChild(usernameCell);
        var commentCell = document.createElement("td")
        commentCell.textContent = comment.text;
        row.appendChild(commentCell)
        table.insertBefore(row, table.rows[0]);  // add new comment to the top
      }

      // update the album array
      var imageIdInt = parseInt(imageId);
      chunkedAlbum.forEach(function(chunk) {
        chunk.forEach(function (image) {
          if(image.id === imageIdInt) {
            image.commentsList.unshift(comment); // add to the top
          }
        });
      });

    }

    this.show = function(albumid) {
      var self = this;
      makeCall("GET", "GetAlbum?albumid=" + albumid, null,
        function(req) {
          if (req.readyState === 4) {
            var message = req.responseText;
            if (req.status === 200) {
              var album = JSON.parse(req.responseText);
              chunkedAlbum = self.chunkArray(album, 5);
              self.update(0);
            } else {
              self.alert.textContent = message;
            }
          }
        }
      );
    }

    this.chunkArray = function(album, chunk_size){
      var results = [];
      while (album.length) {
        results.push(album.splice(0, chunk_size));
      }
      return results;
    }

    this.reset = function() {
      this.imagescontainer.style.visibility = "hidden";
      this.imagedetailscontainer.style.visibility = "hidden";
    }

    this.update = function(groupId) {
      var groups_quantity = chunkedAlbum.length;
      if(groups_quantity === 0) {
        this.imagescontainerbody.innerHTML = ""; // empty the table body
        this.alert.textContent = "No images yet!";
      }
      if (groupId >= 0 && groupId <= groups_quantity-1) {
        var currentGroup = chunkedAlbum[groupId];
        var l = currentGroup.length,
            row, imagecell
        if (l === 0) {
          this.alert.textContent = "No images yet!";
        } else {
          this.alert.textContent = "";
          this.imagescontainerbody.innerHTML = ""; // empty the table body
          // build updated list
          var self = this;

          row = document.createElement("tr");
          currentGroup.forEach(function (image) { // self visible here, not this
            imagecell = document.createElement("td");
            var img = document.createElement('img');
            img.className = "thumbnail";
            img.src = image.filepath;
            imagecell.appendChild(img);
            self.addImageMouseActions(img, image);
            row.appendChild(imagecell);
          });

          // add previous and next button
          if (groupId > 0) {
            var previous_btn_cell = row.insertCell(0)
            previousButton.setAttribute("groupId", (groupId-1).toString());
            previous_btn_cell.appendChild(previousButton);
          }

          if (groupId < groups_quantity-1) {
            var next_btn_cell = row.insertCell(-1)
            nextButton.setAttribute("groupId", (groupId + 1).toString());
            next_btn_cell.appendChild(nextButton);
          }

          self.imagescontainerbody.appendChild(row);
          self.imagescontainer.style.visibility = "visible";
        }
      }
    }

    var isClicked = false;
    var clickedImgId = -1;
    this.addImageMouseActions = function(img, imageData) {
      var self = this;

      img.addEventListener('mouseover', (e) => {

        if (imageData.id !== clickedImgId) {
          isClicked = false;
          clickedImgId = -1;

          // image details
          var imagedetails = self.imagedetailscontainer.querySelector('.imagedetails');
          imagedetails.querySelector('.title').textContent = imageData.name;
          imagedetails.querySelector('.date').textContent = imageData.date
          imagedetails.querySelector('.description').textContent = imageData.description;
          var imgDOM = imagedetails.querySelector('.full-screen-image');
          imgDOM.src = imageData.filepath;
          imgDOM.alt = imageData.name;
          imgDOM.title = imageData.name;

          // image comments
          var table = self.imagedetailscontainer.querySelector('.commentsdetails .comments-table');
          table.innerHTML = '';
          imageData.commentsList.forEach(function (comment) {
            var row = document.createElement("tr");
            var usernameCell = document.createElement("th")
            usernameCell.textContent = 'Comment by ' + comment.username;
            row.appendChild(usernameCell);
            var commentCell = document.createElement("td")
            commentCell.textContent = comment.text;
            row.appendChild(commentCell)
            table.appendChild(row);
          });

          // update form value
          document.getElementById('comment-form').querySelector("input[name='imageid']").value = imageData.id;

          // show container
          self.imagedetailscontainer.style.visibility = "visible";
        }
      });

      img.addEventListener('mouseout', (e) => {
        // hide container
        if(!isClicked) {
          self.imagedetailscontainer.style.visibility = "hidden";
        }
      })

      img.addEventListener('click', (e) => {
        // show container
        isClicked = true;
        clickedImgId = imageData.id;
        self.imagedetailscontainer.style.visibility = "visible";
      })

    }
  }

  function PageOrchestrator() {
    var alertContainer = document.getElementById("id_alert")

    this.start = function() {
      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
          document.getElementById("id_username"));
      personalMessage.show();

      albumsList = new AlbumsList(
        alertContainer,
        document.getElementById("id_listcontainer"),
        document.getElementById("id_listcontainerbody"));

      albumDetails = new AlbumDetails({ // many parameters, wrap them in an object
        alert: alertContainer,
        imagescontainer: document.getElementById("id_imagescontainer"),
        imagescontainerbody: document.getElementById("id_imagescontainerbody"),
        imagedetailscontainer: document.getElementById("id_imagedetailscontainer"),
        commentform: document.getElementById("comment-form"),
      });
    };

    this.refresh = function() {
      albumsList.reset();
      albumDetails.reset();
      albumsList.show();
    };
  }

})();
