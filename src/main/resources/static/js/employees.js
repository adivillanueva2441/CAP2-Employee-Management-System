const EMPLOYEE_API = '/api/employees';
const DEPARTMENT_API = '/api/departments';
const PAGE_SIZE = 10;

let currentPage = 0;
let currentFilter = {};

// runs when the page finishes loading
document.addEventListener('DOMContentLoaded', () => {
    loadEmployees();
    loadDepartments();
    loadStats();
});

async function loadDepartments(){
    const res = await fetch(`${DEPARTMENT_API}?size=100`, {credentials: 'include'});
    const data = await res.json();

    data.content.forEach(department => {
        document.getElementById('filterDepartment')
        .innerHTML +=
            `<option value = "${department.departmentId}">${department.departmentName}</option>`;
        
    });
}

// fetches employees from the API and renders them
async function loadEmployees(page = 0, filter = {}) {
    currentPage = page;
    currentFilter = filter;

    let url = `${EMPLOYEE_API}?page=${page}&size=${PAGE_SIZE}`;

    if(filter.departmentId && filter.minAge && filter.maxAge){
        url = `${EMPLOYEE_API}/filter?departmentId=${filter.departmentId}&minAge=${filter.minAge}&maxAge=${filter.maxAge}&page=${page}&size=${PAGE_SIZE}`
    }else if(filter.departmentId){
        url = `${EMPLOYEE_API}/filter?departmentId=${filter.departmentId}&page=${page}&size=${PAGE_SIZE}`
    }else if(filter.minAge && filter.maxAge){
        url = `${EMPLOYEE_API}/filter?&minAge=${filter.minAge}&maxAge=${filter.maxAge}&page=${page}&size=${PAGE_SIZE}`
    }

    const res = await fetch(
        url, {credentials: 'include'});

    const data = await res.json();

    renderTable(data);
    renderPagination(data);
}

async function searchEmployees(employeeName, page = 0) {
    const res = await fetch(
        `${EMPLOYEE_API}/search?employeeName=${encodeURIComponent(employeeName)}&page=${page}&size=${PAGE_SIZE}`,
        { credentials: 'include' }
    );
    const data = await res.json();
 
    renderTable(data);
    renderPagination(data);
}

// builds the table rows from the API response
function renderTable(data) {
    const tbody = document.getElementById('employeeTableBody');

    if (data.content.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-5">No employees found.</td>
            </tr>`;
        return;
    }

    // loop through each employee and build a table row
    tbody.innerHTML = data.content.map(employee => `
        <tr>
            <td class="ps-3">${employee.employeeNumber}</td>
            <td>${employee.name}</td>
            <td>${employee.department}</td>
            <td>${employee.dateOfBirth}</td>
            <td>${employee.age}</td>
            <td>₱${parseFloat(employee.salary).toLocaleString()}</td>
            <td>
                <a href="/employee-form.html?id=${employee.employeeId}" class="btn btn-outline-primary btn-sm me-1">
                    <i class="bi bi-pencil"></i>
                </a>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteEmployee(${employee.employeeId})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// builds the pagination buttons
function renderPagination(data) {
    const { number, totalPages, totalElements, size } = data;

    // show "Showing 1-10 of 100 employees"
    const start = number * size + 1;
    const end = Math.min((number + 1) * size, totalElements);
    document.getElementById('paginationInfo').textContent =
        totalElements > 0 ? `Showing ${start}–${end} of ${totalElements} employees` : '';

    const pagination = document.getElementById('pagination');

    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';

    // previous button
    html += `
        <li class="page-item ${number === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="event.preventDefault(); loadEmployees(${number - 1}, currentFilter)">
                <i class="bi bi-chevron-left"></i>
            </a>
        </li>`;

    // page number buttons
    for (let i = 0; i < totalPages; i++) {
        html += `
            <li class="page-item ${i === number ? 'active' : ''}">
                <a class="page-link" href="#" onclick="event.preventDefault(); loadEmployees(${i}, currentFilter)">${i + 1}</a>
            </li>`;
    }

    // next button
    html += `
        <li class="page-item ${number === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="event.preventDefault(); loadEmployees(${number + 1}, currentFilter)">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>`;

    pagination.innerHTML = html;
}

// calls searchEmployees when user types, clears back to full list when empty
document.getElementById('searchInput').addEventListener('input', () => {
    const employeeName = document.getElementById('searchInput').value.trim();
 
    if (!employeeName) {
        // search cleared — reload normal employee list
        loadEmployees(0, currentFilter);
        return;
    }
 
    searchEmployees(employeeName);
});

//Filter button 
document.getElementById("applyFilterBtn").addEventListener('click', () =>{
    const departmentId = document.getElementById('filterDepartment').value;
    const minAge = document.getElementById('filterMinAge').value;
    const maxAge = document.getElementById('filterMaxAge').value;
    const errorDiv = document.getElementById('filterError');

    errorDiv.classList.add('d-none');

        // both ages must be provided together
    if ((minAge && !maxAge) || (!minAge && maxAge)) {
        errorDiv.textContent = 'Both min and max age must be provided together.';
        errorDiv.classList.remove('d-none');
        return;
    }

    // minAge cannot be greater than max
    if (minAge && maxAge && parseInt(minAge) > parseInt(maxAge)) {
        errorDiv.textContent = 'Minimum age cannot be greater than maximum age.';
        errorDiv.classList.remove('d-none');
        return;
    }

    // minAge and maxAge must be between 18 and 80
    if (minAge && maxAge && (parseInt(minAge) < 18 || parseInt(maxAge) < 18 || parseInt(minAge) > 80 || parseInt(maxAge) > 80)){
        errorDiv.textContent = 'Minimum and Max Age must be between 18 and 80.';
        errorDiv.classList.remove('d-none');
        return;
    }

    //filter object
    const filter = {};
    if (departmentId) filter.departmentId = departmentId;
    if (minAge) filter.minAge = minAge;
    if (maxAge) filter.maxAge = maxAge;

    loadEmployees(0, filter); //Reset filter when no filters are selected

});

//Clear filters
document.getElementById('clearFilterBtn').addEventListener('click', () => {
    document.getElementById('filterDepartment').value = '';
    document.getElementById('filterMinAge').value = '';
    document.getElementById('filterMaxAge').value = '';
    document.getElementById('filterError').classList.add('d-none');
    loadEmployees(0, {});
});

// Fetches average salary and age from the report API
async function loadStats() {
    const salaryRes = await fetch('/api/reports/average-salary', { credentials: 'include' });
    const ageRes = await fetch('/api/reports/average-age', { credentials: 'include' });
    const totalEmpRes = await fetch('/api/reports/total-employees', { credentials: 'include' });
    const totalDeptRes = await fetch('/api/reports/total-departments', { credentials: 'include' });

    const avgSalary = await salaryRes.json();
    const avgAge = await ageRes.json();
    const totalEmployees = await totalEmpRes.json();
    const totalDepartments = await totalDeptRes.json();

    document.getElementById('statAvgSalary').textContent =
        '₱' + parseFloat(avgSalary).toLocaleString('en-PH', { minimumFractionDigits: 2 });
    document.getElementById('statAvgAge').textContent =
        parseFloat(avgAge).toFixed(1) + ' yrs';
    document.getElementById('statTotalEmployees').textContent = totalEmployees;
    document.getElementById('statTotalDepartments').textContent = totalDepartments;
}

// Deletes an employee by id
async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to delete this employee?')) return;

    const res = await fetch(`${EMPLOYEE_API}/${id}`, {
        method: 'DELETE',
        credentials: 'include'
    });

    if (res.ok) {
        loadEmployees(currentPage); // Reload current page after delete
    } else {
        alert('Failed to delete employee.');
    }
}