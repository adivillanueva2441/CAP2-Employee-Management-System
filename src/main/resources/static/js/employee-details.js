const EMPLOYEE_API = '/api/employees';

// get the employee id from the URL
const urlParams = new URLSearchParams(window.location.search);
const employeeId = urlParams.get('id');

// ── WHEN PAGE LOADS ──
document.addEventListener('DOMContentLoaded', () => {
    // if no id in URL redirect back to list
    if (!employeeId) {
        window.location.href = '/employees.html';
        return;
    }
    loadEmployee(employeeId);
});

// ── LOAD EMPLOYEE ──
// fetches the employee data and fills in the details
async function loadEmployee(id) {
    const res = await fetch(`${EMPLOYEE_API}/${id}`, { credentials: 'include' });

    if (!res.ok) {
        alert('Employee not found.');
        window.location.href = '/employees.html';
        return;
    }

    const employee = await res.json();

    // fill in all the detail fields
    document.getElementById('employeeName').textContent = employee.name;
    document.getElementById('employeeNumber').textContent = employee.employeeNumber;
    document.getElementById('department').textContent = employee.department;
    document.getElementById('dateOfBirth').textContent = employee.dateOfBirth;
    document.getElementById('age').textContent = employee.age + ' years old';
    document.getElementById('salary').textContent =
        '₱' + parseFloat(employee.salary).toLocaleString('en-PH', { minimumFractionDigits: 2 });

    // set the edit button href
    document.getElementById('editBtn').href = `/employee-form.html?id=${employee.employeeId}`;

    // wire up the delete button
    document.getElementById('deleteBtn').addEventListener('click', () => {
        deleteEmployee(employee.employeeId);
    });
}

// ── DELETE EMPLOYEE ──
async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to permanently delete this employee? This action cannot be undone.')) return;

    const res = await fetch(`${EMPLOYEE_API}/${id}`, {
        method: 'DELETE',
        credentials: 'include'
    });

    if (res.ok) {
        // redirect back to employee list after delete
        sessionStorage.setItem('successMessage', 'Employee deleted successfully.');
        window.location.href = '/employees.html';
    } else {
        const err = await res.json();
        alert(err.message || 'Failed to delete employee.');
    }
}