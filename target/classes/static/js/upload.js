$(document).ready(function () {
    var fileList = []; // This will hold the file objects

    // Handle file input changes to display file names
    $('#files').on('change', function () {
        var files = $(this)[0].files;
        for (var i = 0; i < files.length; i++) {
            fileList.push(files[i]); // Add each file to the fileList array
        }
        updateFileList(); // Update the file list display
    });

    // Function to display a file name with a delete button
    function displayFile(file, index) {
        var fileItem = `
            <div class="file-item">
                <div class="file-icon" data-index="${index}">
                    ${getFileIcon(file.name)}
                </div>
                <button type="button" class="btn btn-sm btn-danger btn-remove-file" data-index="${index}">X</button>
                <div>${file.name}</div>
            </div>`;
        $('#file-names').append(fileItem);
    }

    // Function to get the appropriate icon based on file type
    function getFileIcon(fileName) {
        var fileExtension = fileName.split('.').pop().toLowerCase();
        if (fileExtension === 'pdf') {
            return '<i class="fas fa-file-pdf" style="font-size:48px;color:red"></i>';
        } else if (fileExtension === 'doc' || fileExtension === 'docx') {
            return '<i class="fas fa-file-word" style="font-size:48px;color:blue"></i>';
        } else {
            return '<i class="fas fa-file-alt" style="font-size:48px;color:gray"></i>';
        }
    }

    // Function to remove a file from the list
    $('#file-names').on('click', '.btn-remove-file', function () {
        var index = $(this).data('index');
        fileList.splice(index, 1); // Remove the file from the fileList array

        updateFileList(); // Rebuild the file list display after removal
    });

    // Function to update the file input element with the current file list
    function updateFileInput() {
        var dataTransfer = new DataTransfer();

        fileList.forEach(function (file) {
            dataTransfer.items.add(file);
        });

        $('#files')[0].files = dataTransfer.files; // Update the input with the new files list
    }

    // Function to update the file list display
    function updateFileList() {
        $('#file-names').empty(); // Clear the current list

        // Re-display each file with the correct index
        fileList.forEach(function (file, index) {
            displayFile(file, index);
        });

        updateFileInput(); // Update the file input element
    }

    // Preview the selected file in a new tab
    $('#file-names').on('click', '.file-icon', function () {
        var index = $(this).data('index');
        var file = fileList[index];
        var fileURL = URL.createObjectURL(file);

        // Open the file in a new tab
        window.open(fileURL);
    });

    // Function to add a new detail row
    function addDetailRow() {
        var index = $('.detail-row').length;
        var detailRow = `
            <tr class="detail-row">
                <td><input type="text" name="details[` + index + `].text" placeholder="Text" /></td>
                <td>
                    <select name="details[` + index + `].cases">
                        <option value="Criminal">Criminal</option>
                        <option value="Civil">Civil</option>
                        <option value="ContractDisputes">Contract Disputes</option>
                        <option value="Appeals">Appeals</option>
                        <option value="Lawsuit">Lawsuit</option>
                    </select>
                </td>
                <td>
                    <select name="details[` + index + `].priorityLevel">
                        <option value="URGENT">URGENT</option>
                        <option value="HIGH">HIGH</option>
                        <option value="NORMAL">NORMAL</option>
                        <option value="LOW">LOW</option>
                    </select>
                </td>
                <td><input type="date" name="details[` + index + `].startDate" /></td>
                <td><input type="date" name="details[` + index + `].endDate" /></td>
                <td>
                <button class="btnt">
  <svg viewBox="0 0 15 17.5" height="17.5" width="15" xmlns="http://www.w3.org/2000/svg" class="icon">
  <path transform="translate(-2.5 -1.25)" d="M15,18.75H5A1.251,1.251,0,0,1,3.75,17.5V5H2.5V3.75h15V5H16.25V17.5A1.251,1.251,0,0,1,15,18.75ZM5,5V17.5H15V5Zm7.5,10H11.25V7.5H12.5V15ZM8.75,15H7.5V7.5H8.75V15ZM12.5,2.5h-5V1.25h5V2.5Z" id="Fill"></path>
</svg>
</button>
                
                </td>

              
                            <td>
                              
                                <button class="icon-btn add-btn"  id="add-detail-row">
                                    <div class="add-icon"></div>
                                    <div class="btn-txt">Add Row</div>
                                </button> 
                                
                            </td>
              
            </tr>`;
        $('#details-table tbody').append(detailRow);
    }

    // Handle adding a new row
    $(document).on('click', '#add-detail-row', addDetailRow);

    // Handle deleting a row
    $('#details-table').on('click', '.btnt', function () {
        $(this).closest('tr').remove();
    });

    // Initialize Select2
    $('.select2').select2({
        placeholder: "Select a customer",
        allowClear: true
    });
});
