// Check if there is an id in the URL to know if the form is for adding employee or editing existing employee
const urlParams = new URLSearchParams(window.location.search);
const employeeId = urlParams.get('id');
const isEditing = employeeId !== null;

// Checks if page has loaded
document.addEventListener('DOMContentLoaded', () => {
    loadDepartments();

    //Will only execute if there is no id in the URL
    if (isEditing) {
        // update page title and button text for edit mode
        document.getElementById('pageTitle').textContent = 'Edit Employee';
        document.getElementById('pageSubtitle').textContent = 'Update the details below';
        document.getElementById('saveBtn').innerHTML = '<i class="bi bi-check-lg me-1"></i> Update Employee';

        // load the employee data and fill the form
        loadEmployee(employeeId);
    }
});


//Populates department dropdown field
async function loadDepartments() {
    const res = await fetch(`${DEPARTMENT_API}?size=100`, { credentials: 'include' });
    const data = await res.json();

    data.content.forEach(dept => {
        document.getElementById('fieldDepartment').innerHTML +=
            `<option value="${dept.departmentId}">${dept.departmentName}</option>`;
    });
}


//Fetches the employee data if editing
async function loadEmployee(id) {
    const res = await fetch(`${EMPLOYEE_API}/${id}`, { credentials: 'include' });

    if (!res.ok) {
        showError('Employee not found.');
        return;
    }

    const employee = await res.json();

    // fill the form with the employee's current data
    document.getElementById('fieldName').value = employee.name;
    document.getElementById('fieldDob').value = employee.dateOfBirth;
    document.getElementById('fieldSalary').value = employee.salary;

    // wait for departments to load then select the correct one
    // we match by department name since the response returns name not id
    const select = document.getElementById('fieldDepartment');
    const interval = setInterval(() => {
        for (let option of select.options) {
            if (option.text === employee.department) {
                option.selected = true;
                clearInterval(interval);
                break;
            }
        }
    }, 100);
}

// Handles both add and edit depending on isEditing
document.getElementById('saveBtn').addEventListener('click', async () => {
    const name = document.getElementById('fieldName').value.trim();
    const departmentId = document.getElementById('fieldDepartment').value;
    const dateOfBirth = document.getElementById('fieldDob').value;
    const salary = document.getElementById('fieldSalary').value;

    // field validation
    if (!name || !departmentId || !dateOfBirth || !salary) {
        showError('Please fill in all fields.');
        return;
    }

    // build the request body
    const body = {
        name: name,
        departmentId: parseInt(departmentId),
        dateOfBirth: dateOfBirth,
        salary: parseFloat(salary)
    };

    // decide whether to POST (add) or PUT (edit)
    const url = isEditing ? `${EMPLOYEE_API}/${employeeId}` : EMPLOYEE_API;
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
    sessionStorage.setItem('successMessage', message);
    window.location.href = PAGE_EMPLOYEES;
});

function showError(message) {
    const alert = document.getElementById('errorAlert');
    document.getElementById('errorMessage').textContent = message;
    alert.classList.remove('d-none');
}