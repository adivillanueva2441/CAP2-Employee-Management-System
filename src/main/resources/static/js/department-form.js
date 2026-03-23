// Check if there is an id in the URL to know if the form is for adding department or editing existing
const urlParams = new URLSearchParams(window.location.search);
const departmentId = urlParams.get('id');
const isEditing = departmentId !== null;

// Checks if page has loaded
document.addEventListener('DOMContentLoaded', () => {
    if (isEditing) {
        // update page title and button text for edit mode
        document.getElementById('pageTitle').textContent = 'Edit Department';
        document.getElementById('pageSubtitle').textContent = 'Update the details below';
        document.getElementById('saveBtn').innerHTML = '<i class="bi bi-check-lg me-1"></i> Update Department';

        // load the department data and fill the form
        loadDepartment(departmentId);
    }
});

// fetches the department data and fills the form field
async function loadDepartment(id) {
    const res = await fetch(`${DEPARTMENT_API}/${id}`, { credentials: 'include' });

    if (!res.ok) {
        showError('Department not found.');
        return;
    }

    const dept = await res.json();

    // fill the form with the department's current data
    document.getElementById('fieldDepartmentName').value = dept.departmentName;
}

// handles both add and edit depending on isEditing
document.getElementById('saveBtn').addEventListener('click', async () => {
    const name = document.getElementById('fieldDepartmentName').value.trim();

    //validation
    if (!name) {
        showError('Please enter a department name.');
        return;
    }

    // build the request body
    const body = {
        departmentName: name
    };

    // decide whether to POST (add) or PUT (edit)
    const url = isEditing ? `${DEPARTMENT_API}/${departmentId}` : DEPARTMENT_API;
    const method = isEditing ? 'PUT' : 'POST';

    const res = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        const err = await res.json();
        showError(err.message || 'Something went wrong.');
        return;
    }

        //read success message from message.properties
        let message;
        if (isEditing) {
            // PUT returns message in response body
            message = await res.text();
        } else {
            // POST returns message in X-Success-Message header
            message = res.headers.get('X-Success-Message');
        }

    // success — redirect back to department list
    sessionStorage.setItem('successMessage', message);
    window.location.href = PAGE_DEPARTMENTS;
});

function showError(message) {
    const alert = document.getElementById('errorAlert');
    document.getElementById('errorMessage').textContent = message;
    alert.classList.remove('d-none');
}